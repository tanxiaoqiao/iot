package com.honeywell.fireiot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.honeywell.fireiot.constant.FileImportAction;
import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.fire.service.FireDailyDataService;
import com.honeywell.fireiot.repository.*;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.FileUploadService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.utils.*;
import com.honeywell.fireiot.water.service.WaterDailyEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * 业务设备逻辑处理
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:57 AM
 */
@Service
@Slf4j
public class BusinessDeviceServiceImpl implements BusinessDeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessDeviceServiceImpl.class);

    @Autowired
    BusinessDeviceRepository businessDeviceRepo;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    LocationService locationService;

    @Autowired
    SystemTypeRepository systemTypeRepository;
    @Autowired
    DeviceTypeRepository deviceTypeRepo;

    @Autowired
    FireDeviceRepository fireDeviceRepository;

    @Autowired
    WaterDailyEventService waterDailyEventService;
    @Autowired
    FireDailyDataService fireDailyDataService;
    @Autowired
    FileUploadService fileUploadService;

    @Override
    public void save(BusinessDevice entity) {
        businessDeviceRepo.save(entity);
    }

    @Override
    public void save(BusinessDeviceDto dto) {
        BusinessDevice entity = new BusinessDevice();
        entity =dto2Entity(dto, entity);
        businessDeviceRepo.save(entity);
    }

    @Override
    public void delete(BusinessDevice t) {
        businessDeviceRepo.delete(t);
    }

    @Override
    public void deleteById(Long id) {
        businessDeviceRepo.deleteById(id);
    }

    @Override
    public void deleteById(Long[] ids) {
        for (Long id : ids) {
            businessDeviceRepo.deleteById(id);
        }
    }

    @Override
    public boolean update(BusinessDeviceDto dto) {
        Optional<BusinessDevice> op = businessDeviceRepo.findById(dto.getId());
        if (op.isPresent()) {
            BusinessDevice entity = op.get();
            dto2Entity(dto, entity);
            businessDeviceRepo.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public BusinessDeviceDto findById(Long id) {
        Optional<BusinessDevice> opt = businessDeviceRepo.findById(id);
        BusinessDeviceDto businessDeviceDto = null;
        if (opt.isPresent()) {
            businessDeviceDto = toDto(opt.get());
        }
        return businessDeviceDto;
    }

    @Override
    public Pagination<BusinessDeviceDto> findPage() {
        Page<BusinessDevice> page = businessDeviceRepo.findAll(JpaUtils.getPageRequest());
        Pagination<BusinessDeviceDto> pagination = getBusinessDeviceDtoPagination(page);
        return pagination;
    }

    @Override
    public Pagination<BusinessDeviceDto> findPage(Specification<BusinessDevice> specification) {
        Page<BusinessDevice> page = businessDeviceRepo.findAll(specification, JpaUtils.getPageRequest());

        Pagination<BusinessDeviceDto> pagination = getBusinessDeviceDtoPagination(page);
        return pagination;
    }

    @Override
    public List<BusinessDeviceDto> find(Specification<BusinessDevice> specification){
        List<BusinessDevice>  results =businessDeviceRepo.findAll(specification);
        List<BusinessDeviceDto> dtoList = new ArrayList<BusinessDeviceDto>();
        for (BusinessDevice businessDevice : results) {
            BusinessDeviceDto businessDeviceDto = toDto(businessDevice);
            dtoList.add(businessDeviceDto);
        }
        return dtoList;
    }
//    @Override
//    public Optional<BusinessDevice> findById(Long id) {
//        Optional<BusinessDevice> opt = businessDeviceRepo.findById(id);
//        return opt;
//    }

//    @Override
//    public Page<BusinessDevice> findPage() {
//        Page<BusinessDevice> page = businessDeviceRepo.findAll(JpaUtils.getPageRequest());
//        return page;
//    }

//    @Override
//    public Page<BusinessDevice> findPage(Specification<BusinessDevice> specification) {
//        Page<BusinessDevice> page = businessDeviceRepo.findAll(specification, JpaUtils.getPageRequest());
//        return page;
//    }

    @Override
    public BusinessDeviceDto toDto(BusinessDevice entity) {
        BusinessDeviceDto dto = new BusinessDeviceDto();
        BeanUtils.copyProperties(entity, dto);
        // 封装系统类型
        SystemTypeDto systemType = new SystemTypeDto();
        if (entity.getSystemType() != null) {
            BeanUtils.copyProperties(entity.getSystemType(), systemType);
            dto.setSystemType(systemType);
        }
        // 封装设备类型
        DeviceTypeDto deviceTypeDto = new DeviceTypeDto();

        if (entity.getDeviceType() != null) {
            BeanUtils.copyProperties(entity.getDeviceType(), deviceTypeDto);
            dto.setDeviceType(deviceTypeDto);
        }
        return dto;
    }

    /**
     * 查询设备数量
     *
     * @param systemType
     * @param deviceType
     * @return
     */
    @Override
    public Map<String, Object> getDeviceCount(String systemType, String building, String floor, String deviceType) {

        boolean queryAllLocation = false;
        Long locationId = locationService.findIdByBuildingAndFloor(building, floor);

        // 当查询的楼、层不存在时，返回null
        if (building != null || floor != null) {
            if (locationId == null) {
                return Collections.emptyMap();
            }
        } else {
            queryAllLocation = true;
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        if (queryAllLocation && StringUtils.isEmpty(deviceType)) {
            resultList = businessDeviceRepo.getDeviceCount("%" + systemType.trim() + "%");
        } else if (queryAllLocation && StringUtils.isNotEmpty(deviceType)) {
            resultList = businessDeviceRepo.getDeviceCount("%" + systemType.trim() + "%", deviceType);
        } else if (!queryAllLocation && StringUtils.isEmpty(deviceType)) {
            resultList = businessDeviceRepo.getDeviceCount("%" + systemType.trim() + "%", locationId);
        } else if (!queryAllLocation && StringUtils.isNotEmpty(deviceType)) {
            resultList = businessDeviceRepo.getDeviceCount("%" + systemType.trim() + "%", locationId, deviceType);
        }

        Map<String, Object> deviceCount = new HashMap<String, Object>();
        for (Map<String, Object> map : resultList) {
            deviceCount.put(map.get("name").toString(), map.get("value"));
        }
        return deviceCount;
    }

    /**
     * 查询设备信息
     *
     * @param systemType
     * @param building
     * @param floor
     * @param deviceType
     * @return
     */
    @Override
    public List<BusinessDevice> getDevice(String systemType, String building, String floor, String deviceType) {

        boolean queryAllLocation = false;
        Long locationId = locationService.findIdByBuildingAndFloor(building, floor);

        // 当查询的楼、层不存在时，返回null
        if (StringUtils.isNotEmpty(building) || StringUtils.isNotEmpty(floor)) {
            if (locationId == null) {
                return Collections.emptyList();
            }
        } else {
            queryAllLocation = true;
        }

        List<BusinessDevice> resultList = new ArrayList<>();

        if (queryAllLocation && StringUtils.isEmpty(deviceType)) {
            resultList = businessDeviceRepo.getDevice("%" + systemType.trim() + "%");
        } else if (queryAllLocation && StringUtils.isNotEmpty(deviceType)) {
            resultList = businessDeviceRepo.getDevice("%" + systemType.trim() + "%", deviceType);
        } else if (!queryAllLocation && StringUtils.isEmpty(deviceType)) {
            resultList = businessDeviceRepo.getDevice("%" + systemType.trim() + "%", locationId);
        } else if (!queryAllLocation && StringUtils.isNotEmpty(deviceType)) {
            resultList = businessDeviceRepo.getDevice("%" + systemType.trim() + "%", locationId, deviceType);
        }

        return resultList;
    }

    @Override
    public BusinessDevice findByNo(String deviceNo) {
        List<BusinessDevice> deviceList = businessDeviceRepo.findByDeviceNo(deviceNo);
        if (!deviceList.isEmpty()) {
            return deviceList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<BusinessDevice> findByDeviceType(DeviceType deviceType) {
        return businessDeviceRepo.findAllByDeviceType(deviceType);
    }

    @Override
    public List<BusinessDevice> findByLocationId(Long locationId) {
        return businessDeviceRepo.findByLocationId(locationId);
    }

    @Override
    public BusinessLocationDto toBLDto(BusinessDevice entity) {
        BusinessLocationDto businessLocationDto = new BusinessLocationDto();
        BeanUtils.copyProperties(entity, businessLocationDto);
        businessLocationDto.setDeviceId(entity.getDeviceNo());
        if (entity.getDeviceType() != null) {
            businessLocationDto.setDeviceType(entity.getDeviceType().getName());
        }
        if (entity.getSystemType() != null) {
            businessLocationDto.setSystemType(entity.getSystemType().getName());
        }
        Location location = locationRepository.findAllById(entity.getLocationId());
        if (location != null) {
            String[] ss = location.getFullName().split("/");
            if (ss.length == 1) {
                businessLocationDto.setBuilding(ss[0]);
            } else if (ss.length == 2) {
                businessLocationDto.setBuilding(ss[0]);
                businessLocationDto.setFloor(ss[1]);
            }
        }
        return businessLocationDto;
    }


    @Override
    public Pagination<BusinessLocationDto> getBusinessLocation(Specification<BusinessDevice> specification) {

        Page<BusinessDevice> page = businessDeviceRepo.findAll(specification, JpaUtils.getPageRequest());
        List<BusinessLocationDto> businessLocationDtoList = new ArrayList<>();
        page.getContent().forEach(businessDevice ->
                businessLocationDtoList.add(toBLDto(businessDevice))
        );
        Pagination pages = new Pagination((int) page.getTotalElements(), businessLocationDtoList);
        return pages;
    }

    @Override
    public void setMapLocationInfo(MultipartFile file) {

        byte[] data = FileUtil.readFile(file);
        if (null == data) {
            return;
        }

        String str = new String(data);
        JSONArray jsonArray = JSONArray.parseArray(str);
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String deviceNo = jsonObject.getString("_id");
            String mapLocation = jsonObject.getString("htConfig");

            List<BusinessDevice> businessDeviceServiceList = businessDeviceRepo.findByDeviceNo(deviceNo);
            if (!businessDeviceServiceList.isEmpty()) {
                BusinessDevice businessDevice = businessDeviceServiceList.get(0);
                BusinessDevice businessDeviceForSave = businessDeviceRepo.getOne(businessDevice.getId());
                businessDeviceForSave.setMapLocation(mapLocation);
                businessDeviceRepo.save(businessDeviceForSave);
            }

        }
    }

    @Override
    public void updateMapLocation(String mapLocation, String deviceId) {
        businessDeviceRepo.updateMapLocation(mapLocation, deviceId);
    }


    private BusinessDevice dto2Entity(BusinessDeviceDto dto, BusinessDevice entity) {
        BeanUtils.copyProperties(dto, entity);
        if (dto.getSystemType() != null) {
            entity.setSystemType(systemTypeRepository.findById(dto.getSystemType().getId()).get());
        }
        if (dto.getDeviceType() != null) {
            entity.setDeviceType(deviceTypeRepo.findById(dto.getDeviceType().getId()).get());
        }
        return entity;

    }

    private Pagination<BusinessDeviceDto> getBusinessDeviceDtoPagination(Page<BusinessDevice> page) {
        List<BusinessDevice> entityList = page.getContent();
        List<BusinessDeviceDto> dtoList = new ArrayList<BusinessDeviceDto>();
        for (BusinessDevice businessDevice : entityList) {
            BusinessDeviceDto businessDeviceDto = toDto(businessDevice);
            dtoList.add(businessDeviceDto);
        }
        return new Pagination<>((int) page.getTotalElements(), dtoList);
    }


    @Override
    public BusinessFireDeviceDto toBFDDto(BusinessDevice entity) {
        BusinessFireDeviceDto businessFireDeviceDto = new BusinessFireDeviceDto();
        BeanUtils.copyProperties(entity, businessFireDeviceDto);
        if (entity.getDeviceType() != null) {
            businessFireDeviceDto.setDeviceType(entity.getDeviceType().getName());
        }
        if (entity.getSystemType() != null) {
            businessFireDeviceDto.setSystemType(entity.getSystemType().getName());
        }
        if (entity.getLocationId() != null) {
            // 这里LocationId可能是Null,自动转换成long会报空指针
            Location location = locationRepository.findAllById(entity.getLocationId());
            if (location != null) {
                businessFireDeviceDto.setFloor(location.getFloor());
                businessFireDeviceDto.setBuilding(location.getBuilding());
            }
        }
        FireDevice fireDevice = fireDeviceRepository.findFirstByBusinessDeviceNo(entity.getDeviceNo());
        if (fireDevice != null) {
            businessFireDeviceDto.setNetwork(fireDevice.getNetwork());
            businessFireDeviceDto.setLoop(fireDevice.getLoop());
            businessFireDeviceDto.setZone(fireDevice.getZone());
            businessFireDeviceDto.setPoint(fireDevice.getPoint());
            businessFireDeviceDto.setStatus(fireDevice.getStatus());
            businessFireDeviceDto.setMtGateway(fireDevice.getMtGateway().getId());
        }
        return businessFireDeviceDto;
    }

    @Override
    public void initSystem() {

        log.info("水火系统30天统计开始");
        // 水火系统日常统计
        for (int i = 30; i >= 1; i--) {
            LocalDate localDate = LocalDate.now().minusDays(i);
            waterDailyEventService.calculateDailyEvent(localDate);
            fireDailyDataService.calculateDailyData(localDate);
        }
        log.info("水火系统30天统计结束");
    }

    public void createQRCode() {
        List<BusinessDevice> list = businessDeviceRepo.findAll();
        //      Page<BusinessDevice> page = businessDeviceRepo.findAll(JpaUtils.getPageRequest());

        for (BusinessDevice businessDevice : list) {
            try {
                String label = businessDevice.getLocationDetail() == null ? "" : businessDevice.getLocationDetail() + ":" + businessDevice.getDeviceType().getName() + ":" + businessDevice.getDeviceNo();
                BufferedImage img = QRcodeUtil.getQRcodeWithNote(businessDevice.getId().toString(), label);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(img, "png", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                Optional<String> url = fileUploadService.uploadFile(is, "fire/QRCode/" + businessDevice.getDeviceNo() + ".png");
                log.info(url.get());
                businessDevice.setQRCode(url.get());
                businessDeviceRepo.save(businessDevice);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void updateLocationTest() {

    }

    public int[] uploadDeviceForPolling(MultipartFile file) throws Exception {
        int[] result = new int[3];
        Workbook workbook =  getWorkBook(file);
        if(workbook==null){
            result[0] = FileImportAction.ERR_FILE;
            return result;
        }
        //excel处理
        return readExcelAndOperateDB(workbook);

    }
    private Workbook getWorkBook(MultipartFile file){
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        // String name = fileName.substring(0,fileName.lastIndexOf("."));

        // readFileConditon.setFileName(name);
        //判断版本
        try {
            if (fileName.endsWith(FileImportAction.SUFFIX_XLS)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith(FileImportAction.SUFFIX_XLSX)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }
    public int[] readExcelAndOperateDB(Workbook workbook) throws Exception {
        int[] result = new int[3];
        LOGGER.info("call readExcelAndOperateDB");
        //2、将数据同步到数据库中
        int sheetNos = workbook.getNumberOfSheets();
        Map<String, String> errorMsg = new HashMap<>();
        Integer succNo = 0;
        for (int i = 0; i < sheetNos; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            int rowNum = sheet.getLastRowNum() + 1;
            LOGGER.info("sheetName" + sheet.getSheetName());
            for (int j = FileImportAction.IGONREROWS_ALLDEVICE; j < rowNum; j++) {
                Row row = sheet.getRow(j);
//            LOGGER.info("read row["+ j+"]");
                if (checkCell(row)) continue;
                //读取每一行
                String line = ExcelUtil.readRowString(row, FileImportAction.CLUMNNUM_ALLDEVICE);
                String[] pills = line.split(",");
                BusinessDevice businessDevice = assembleTheData(errorMsg, pills);
                if (businessDevice != null) {
                    businessDevice = businessDeviceRepo.save(businessDevice);
                    LOGGER.info("businessDevice : " + businessDevice.toString());
                    succNo++;
                }
            }

        }
        LOGGER.info("errorMsgs : " + errorMsg.toString());
        result[0] = FileImportAction.ERR_SUCCESS;
        result[1] = succNo;
        return result;
    }


    private BusinessDevice assembleTheData(Map<String, String> errorMsg, String[] pills) {
        BusinessDevice businessDevice = new BusinessDevice();
        if (!pills[FileImportAction.DEVICE_COLUMN_DEVICENO].trim().isEmpty()) {
            businessDevice.setDeviceNo(pills[FileImportAction.DEVICE_COLUMN_DEVICENO].trim());
        } else {
            return null;
        }
        businessDevice.setDeviceLabel(pills[FileImportAction.DEVICE_COLUMN_DEVICELABEL]);
        businessDevice.setBrandName(pills[FileImportAction.DEVICE_COLUMN_BRANDNAME]);
        // 地址
        String locationName = pills[FileImportAction.DEVICE_COLUMN_TOPAREA].trim();
        String area = pills[FileImportAction.DEVICE_COLUMN_AREA].trim();
        String building = pills[FileImportAction.DEVICE_COLUMN_BUILDING].trim();
        String floor = pills[FileImportAction.DEVICE_COLUMN_FLOOR].trim();
        if (!org.springframework.util.StringUtils.isEmpty(area)) {
            locationName += "/" + area;
        }
        if (!org.springframework.util.StringUtils.isEmpty(building)) {
            locationName += "/" + building;
        }
        if (!org.springframework.util.StringUtils.isEmpty(floor)) {
            locationName += "/" + floor;
        }
        List<Location> locationList = locationRepository.findByFullName(locationName);
        if (!locationList.isEmpty()) {
            businessDevice.setLocationId(locationList.get(0).getId());
            businessDevice.setLocationDetail(locationList.get(0).getFullName());
        }

        //系统类型
        List<SystemType> systemTypeList = systemTypeRepository.findByName(pills[FileImportAction.DEVICE_COLUMN_SYSTEMTYPE].trim());

        if (!systemTypeList.isEmpty()) {
            businessDevice.setSystemType(systemTypeList.get(0));
        }

        //设备类型
        List<DeviceType> deviceTypes = deviceTypeRepo.findByName(pills[FileImportAction.DEVICE_COLUMN_DEVICETYPE].trim());
        if (!deviceTypes.isEmpty()) {
            businessDevice.setDeviceType(deviceTypes.get(0));
        } else {
            errorMsg.put(pills[FileImportAction.DEVICE_COLUMN_DEVICENO].trim(), pills[FileImportAction.DEVICE_COLUMN_DEVICETYPE] + "IS NOT EXIST ");
            businessDevice = null;
        }
        return businessDevice;
    }

    private boolean checkCell(Row row) {
        if (null == row || (row.getFirstCellNum() < 0)) {
            LOGGER.error("row is empty or null");
            return true;
        }
        return false;
    }


    public int[] readFileAndSaveData(MultipartFile file)throws Exception {
        int[] result = new int[3];
        Workbook workbook =  getWorkBook(file);
        if(workbook==null){
            result[0] = FileImportAction.ERR_FILE;
            return result;
        }
        int sheetNos = workbook.getNumberOfSheets();
        Integer succNo = 0;
        for (int i = 0; i < sheetNos; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            int rowNum = sheet.getLastRowNum() + 1;
            LOGGER.info("sheetName" + sheet.getSheetName());
            for (int j = 1; j < rowNum; j++) {
                Row row = sheet.getRow(j);
                if (checkCell(row)) continue;
                //读取每一行
                String line = ExcelUtil.readRowString(row, FileImportAction.CLUMNNUM_ALLDEVICE);
                String[] pills = line.split(",");
               Optional<BusinessDevice> op = businessDeviceRepo.findById(Long.valueOf(pills[0]));
               if(op.isPresent()){
                   BusinessDevice businessDevice = op.get();
                   businessDevice.setDeviceNo(pills[1]);
                   businessDevice.setDeviceLabel(pills[2]);
                   List<DeviceType> deviceTypes = deviceTypeRepo.findByName(pills[3].trim());
                   if (!deviceTypes.isEmpty()) {
                       businessDevice.setDeviceType(deviceTypes.get(0));
                   }
                   List<Location> locationList = locationRepository.findByFullName(pills[4].trim());
                   if (!locationList.isEmpty()) {
                       businessDevice.setLocationId(locationList.get(0).getId());
                       businessDevice.setLocationDetail(locationList.get(0).getFullName());
                   }
                   businessDeviceRepo.save(businessDevice);
                   succNo++;
               }

            }
        }
        result[0] = FileImportAction.ERR_SUCCESS;
        result[1] = succNo;
        return result;
    }
}
