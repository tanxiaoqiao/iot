package com.honeywell.fireiot.water.service;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.FileImportAction;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.BusinessDeviceRepository;
import com.honeywell.fireiot.repository.LocationRepository;
import com.honeywell.fireiot.repository.WaterDeviceRepository;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.DeviceTypeService;
import com.honeywell.fireiot.service.WaterDeviceService;
import com.honeywell.fireiot.utils.ExcelUtil;
import com.honeywell.fireiot.utils.ReadFileConditon;
import com.honeywell.fireiot.water.entity.WaterField;
import com.honeywell.fireiot.water.util.WaterFieldUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: xiaomingCao
 * @date: 2018/12/29
 */
@Service
@Slf4j
public class WaterService {


    private static final int INDEX_BUILDING = 1;
    private static final int INDEX_DEVICE_NAME = 2;
    private static final int INDEX_HEAD_4 = 3;
    private static final int INDEX_HEAD_2 = 4;
    private static final int INDEX_INPUT = 5;
    private static final int INDEX_EUI = 6;
    private static final int INDEX_ADDRESS = 7;
    private static final int INDEX_LABEL = 8;
    private static final int INDEX_DESCRIPTION = 9;


    private BusinessDeviceService businessDeviceService;

    private DeviceTypeService deviceTypeService;

    private WaterDeviceService waterDeviceService;

    private WaterFieldService waterFieldService;

    private LocationRepository locationRepository;

    private WaterDeviceRepository waterDeviceRepository;

    private BusinessDeviceRepository businessDeviceRepository;


    @Autowired
    public WaterService(BusinessDeviceService businessDeviceService,
                        DeviceTypeService deviceTypeService,
                        WaterDeviceService waterDeviceService,
                        WaterFieldService waterFieldService,
                        LocationRepository locationRepository,
                        WaterDeviceRepository waterDeviceRepository,
                        BusinessDeviceRepository businessDeviceRepository) {
        this.businessDeviceService = businessDeviceService;
        this.deviceTypeService = deviceTypeService;
        this.waterDeviceService = waterDeviceService;
        this.waterFieldService = waterFieldService;
        this.locationRepository = locationRepository;
        this.waterDeviceRepository = waterDeviceRepository;
        this.businessDeviceRepository = businessDeviceRepository;
    }

    /**
     * 批量创建设备，
     * 创建Fire IOT中Business Device，并关联Location
     * 创建Fire IOT中Water Device记录
     * 创建WATER中 Water Field记录，
     *
     * @param file
     * @param nameMapper
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchCreate(MultipartFile file, Map<String, String> nameMapper, int colsize, int startrow) throws IOException {
        int colSize = colsize;
        int startRow = startrow;
        ReadFileConditon rfc = new ReadFileConditon();
        rfc.setColumnNum(colSize);
        rfc.setIgonreRowNum(startRow);
        List<List<String>> excelData = getExcelData(file, rfc);
        IntStream.range(0, excelData.size())
                .forEach(rowNumber ->
                    saveExcelRowData(
                            startRow+rowNumber,
                            excelData.get(rowNumber),
                            nameMapper)
                );

    }




    public void saveExcelRowData(int rowNumber , List<String> s, Map<String, String> nameMapper){

        if(StringUtils.isEmpty(s.get(INDEX_EUI))){
            return;
        }

        if(StringUtils.isEmpty(s.get(INDEX_DEVICE_NAME))){
            return;
        }

        if(StringUtils.isEmpty(s.get(INDEX_LABEL))){
            return;
        }

        String eui = s.get(INDEX_EUI);

        // 查找设备是否导入过
        Optional<WaterDevice> waterDeviceOptional = waterDeviceRepository.findByDeviceEUI(eui);
        if(waterDeviceOptional.isPresent()){
            log.error("第"+ rowNumber + "行: 重复导入");
            return;
        }

        // 创建DeviceType
        Optional<DeviceType> optional = getDeviceType(s, nameMapper);
        if(!optional.isPresent()){
            log.error("第"+ rowNumber + "行: 设备类型不存在");
            return;
        }
        DeviceType deviceType = optional.get();


        // 创建BusinessDevice
        BusinessDevice businessDevice = excelData2BusinessDevice(s, deviceType);
        BusinessDevice checkBusinessDeviceExist = businessDeviceService.findByNo(businessDevice.getDeviceNo());
        if(Objects.nonNull(checkBusinessDeviceExist)){
            log.error("第" + rowNumber + "行: 设备编号重复");
            return;
        }


        // 关联Location
        String buildingNameExcel = s.get(INDEX_BUILDING);
        Long locationId = locationRepository.findIdByBuilding(buildingNameExcel);
        businessDevice.setLocationId(locationId);
        businessDevice.setLocationDetail(buildingNameExcel);

        businessDeviceService.save(businessDevice);


        // 创建WaterDevice
        WaterDevice waterDevice = excelData2WaterDevice(s, businessDevice);
        waterDeviceService.save(waterDevice);


        // 创建WaterField
        List<WaterField> waterFields = WaterFieldUtil.getWaterFields(
                deviceType,
                businessDevice.getDeviceNo(),
                waterDevice.getDeviceEUI());
        waterFieldService.saveAll(waterFields);
    }

    private Optional<DeviceType> getDeviceType(List<String> s, Map<String, String> nameMapper){
        log.info(s.get(2));
        String ename = nameMapper.get(s.get(INDEX_DEVICE_NAME));
        String type = "Water";
        log.info(ename + "\t" + type);
        return deviceTypeService.findByNameAndSystemTypeId(ename, 2L);
    }


    private BusinessDevice excelData2BusinessDevice(List<String> s, DeviceType deviceType){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        SystemType systemType = new SystemType();
        systemType.setId(2L);
        BusinessDevice device = new BusinessDevice();
        device.setDeviceType(deviceType);
        device.setDeviceLabel(s.get(INDEX_LABEL));
        device.setSystemType(systemType);
        device.setDateOfCommissioning(new Date());
        device.setBrandName("");
        device.setDeviceNo(uuid);
        device.setDateOfInstallation(new Date());
        device.setDateOfPoduction(new Date());
        device.setDescription(s.get(INDEX_DESCRIPTION));
        return device;
    }

    private WaterDevice excelData2WaterDevice(List<String> s, BusinessDevice businessDevice){
        WaterDevice waterDevice = new WaterDevice();
        waterDevice.setDeviceNo(businessDevice.getDeviceNo());
        waterDevice.setDeviceEUI(s.get(INDEX_EUI));
        waterDevice.setName(s.get(INDEX_DEVICE_NAME));
        waterDevice.setAddress(s.get(INDEX_ADDRESS));
        waterDevice.setLabel(s.get(INDEX_LABEL));
        waterDevice.setInput(s.get(INDEX_INPUT));
        waterDevice.setHead_2(s.get(INDEX_HEAD_2));
        waterDevice.setHead_4(s.get(INDEX_HEAD_4));
        return waterDevice;
    }

    private List<List<String>>  getExcelData(MultipartFile file, ReadFileConditon rfc) throws IOException {

        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        if(fileName.endsWith(".xls")){
            workbook = new HSSFWorkbook(file.getInputStream());
        }else if(fileName.endsWith(".xlsx")){
            workbook = new XSSFWorkbook(file.getInputStream());
        }else if(fileName.endsWith(".csv")){
            // TODO
        }else{
            throw new BusinessException(ErrorEnum.FILE_FORMAT_ERROR);
        }

        int checkResult = ExcelUtil.checkExcel(workbook, rfc);


        if(checkResult != FileImportAction.ERR_SUCCESS){
            throw new BusinessException(ErrorEnum.FILE_FORMAT_ERROR);
        }

        Sheet sheet = workbook.getSheetAt(rfc.getSheetIndex());
        int rowNum = sheet.getLastRowNum() + 1;
        int ignoreRowIndex = rfc.getIgonreRowNum();
        int columnNum = rfc.getColumnNum();

        return IntStream.range(ignoreRowIndex, rowNum)
                .mapToObj(sheet::getRow)
                .filter(Objects::nonNull)
                .map(row -> ExcelUtil.readRowList(row, columnNum))
                .collect(Collectors.toList());
    }


    public Map<String, List<Map<String,String>>> getHtJson(){
        List<WaterDevice> all = waterDeviceRepository.findAll();


        Map<String, List<Map<String,String>>> data = new HashMap<>();



        all.forEach(waterDevice -> {

            List<BusinessDevice> businessDevices = businessDeviceRepository.findByDeviceNo(waterDevice.getDeviceNo());

            if(CollectionUtils.isEmpty(businessDevices)){
                return;
            }

            BusinessDevice businessDevice = businessDevices.get(0);

            if(Objects.isNull(businessDevice)){
                return;
            }

            Long locationId = businessDevice.getLocationId();

            if(Objects.isNull(locationId)){
                return;
            }

            Location location = locationRepository.findAllById(locationId);





            String fullName = location.getFullName();
            String[] split = fullName.split("/");


            String key;

            if(split.length > 1){
                key = split[0]+"-"+split[1];
            }else{
                key = split[0];
            }


            if(StringUtils.isEmpty(key)){
                return;
            }

            List<Map<String, String>> floorDevices = data.get(key);

            if(Objects.isNull(floorDevices)){
                floorDevices = new ArrayList<>();
            }

            Map<String, String> deviceInfo = new HashMap<>();
            deviceInfo.put("name", waterDevice.getLabel());
            deviceInfo.put("_id", waterDevice.getDeviceEUI());
            deviceInfo.put("type", businessDevice.getDeviceType().getName());
            floorDevices.add(deviceInfo);

            data.put(key, floorDevices);



        });

        return data;
    }
}
