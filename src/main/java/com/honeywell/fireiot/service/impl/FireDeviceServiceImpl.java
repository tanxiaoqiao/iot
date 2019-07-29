package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.constant.FileImportAction;
import com.honeywell.fireiot.constant.FireStatusConstant;
import com.honeywell.fireiot.dto.BusinessFireDeviceDto;
import com.honeywell.fireiot.dto.firedevice.DevicePointDto;
import com.honeywell.fireiot.dto.firedevice.FireBusinessDeviceDto;
import com.honeywell.fireiot.dto.firedevice.FireDeviceDto;
import com.honeywell.fireiot.dto.firedevice.FireDevicePointSearch;
import com.honeywell.fireiot.dto.waterdevice.ValueDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceSearch;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.fire.entity.MTGateway;
import com.honeywell.fireiot.fire.entity.MTPoint;
import com.honeywell.fireiot.fire.repository.MTGatewayRepository;
import com.honeywell.fireiot.fire.repository.MTPointRepository;
import com.honeywell.fireiot.repository.*;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.FireDeviceService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.utils.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 3:17 PM 12/17/2018
 */
@Service
public class FireDeviceServiceImpl implements FireDeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireDeviceServiceImpl.class);

    @Autowired
    FireDeviceRepository fireDeviceRepository;

    @Autowired
    MTGatewayRepository mtGatewayRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    DeviceTypeRepository deviceTypeRepository;

    @Autowired
    MTPointRepository mtPointRepository;

    @Autowired
    SystemTypeRepository systemTypeRepository;

    @Autowired
    BusinessDeviceRepository businessDeviceRepository;

    @Autowired
    BusinessDeviceService businessDeviceService;

    @Autowired
    LocationService locationService;

    @Override
    public void save(FireDevice entity) {
        fireDeviceRepository.save(entity);
    }

    @Override
    public List<FireDevice> getAll() {
        return fireDeviceRepository.findAll();
    }

    @Override
    public void add(FireDeviceDto fireDeviceDto) {
        FireDevice fireDevice = new FireDevice();
        toEntity(fireDeviceDto, fireDevice);
        fireDevice.setId(null);
        fireDeviceRepository.save(fireDevice);


    }

    @Override
    public void update(FireDevice fireDevice) {
        FireDevice device = fireDeviceRepository.getOne(fireDevice.getId());
        fireDeviceRepository.save(device);
    }

    @Override
    public FireDevice findById(Long id) {
        Optional<FireDevice> mtDeviceOptional = fireDeviceRepository.findById(id);
        if (mtDeviceOptional.isPresent()) {
            return mtDeviceOptional.get();
        } else {
            return null;
        }
    }

    @Override
    public void deleteById(Long id) {
        fireDeviceRepository.deleteById(id);
    }

    @Override
    public boolean checkUniqueDevice(FireDeviceDto mtDevice) {
        List<FireDevice> fireDeviceList = fireDeviceRepository.findByBusinessDeviceNo(mtDevice.getBusinessDeviceNo());
        if (fireDeviceList.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkUpdateDevice(FireDeviceDto fireDeviceDto) {
        List<FireDevice> fireDeviceList = fireDeviceRepository.findByBusinessDeviceNo(fireDeviceDto.getBusinessDeviceNo());
        if (fireDeviceList.size() > 1) {
            return true;
        }

        if (1 == fireDeviceList.size()) {
            if (fireDeviceDto.getBusinessDeviceNo().equals(fireDeviceList.get(0).getBusinessDeviceNo())) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public DevicePointDto findDevicePointDtoByNo(String deviceNo) {
        DevicePointDto devicePointDto = new DevicePointDto();
        List<FireDevice> fireDeviceList = fireDeviceRepository.findByBusinessDeviceNo(deviceNo);
        if (!fireDeviceList.isEmpty()) {
            FireDevice fireDevice = fireDeviceList.get(0);
            devicePointDto.setLoop(fireDevice.getLoop());
            devicePointDto.setNetwork(fireDevice.getNetwork());
            devicePointDto.setZone(fireDevice.getZone());
            devicePointDto.setPoint(fireDevice.getPoint());
        }

        List<BusinessDevice> businessDeviceList = businessDeviceRepository.findByDeviceNo(deviceNo);
        if (!businessDeviceList.isEmpty()) {
            BusinessDevice businessDevice = businessDeviceList.get(0);
            devicePointDto.setType(businessDevice.getDeviceType().getName());
            devicePointDto.setName(businessDevice.getDeviceLabel());
        }
        return null;
    }


    @Override
    public void checkAndSaveDevice(FireDeviceDto fireDeviceDto) {
        List<FireDevice> deviceList = fireDeviceRepository.findByBusinessDeviceNo(fireDeviceDto.getBusinessDeviceNo());
        //判断系统中是否存在该设备
        if (deviceList.isEmpty()) {
            //添加设备
            add(fireDeviceDto);

        } else {
            LOGGER.info("checkAndSaveDevice device already exist： " + fireDeviceDto.getBusinessDeviceNo());
            //更新
            FireDevice device = fireDeviceRepository.getOne(deviceList.get(0).getId());
            fireDeviceDto.setId(device.getId());
            toEntity(fireDeviceDto, device);
            fireDeviceRepository.save(device);
        }


    }


    @Override
    public void toEntity(FireDeviceDto fireDeviceDto, FireDevice fireDevice) {
        BeanUtils.copyProperties(fireDeviceDto, fireDevice);
        //查找对应的网关
        Optional<MTGateway> mtGatewayOptional = mtGatewayRepository.findById(Long.parseLong(fireDeviceDto.getGatewayNo()));
        if (mtGatewayOptional.isPresent()) {
            fireDevice.setMtGateway(mtGatewayOptional.get());
        }
    }




   /* private int operateDeviceDTO(String line, int operateType, int columnNum, int tableType){

        if(FileImportAction.REMOVE_DBDATA == operateType){

            checkAndDeleteData(line,columnNum, tableType);
            //删除设备
        }else if(FileImportAction.ADD_DBDATA == operateType){
           // checkAndSaveDevice(deviceDto);
            checkAndSaveData(line, columnNum,tableType);
            //删除
        }else {
            return FileImportAction.ERR_OPEARA_NOTEXIST;
        }

        return  FileImportAction.ERR_SUCCESS;
    }*/


    /*
     */

    /*****************提取行中的数据*************************************//*

    private String getDeviceNo(String[] pill){
        String deviceNo = "" ;
        deviceNo = pill[FileImportAction.FIREDEVICE_COLUMN_ID1] + "-" +
                pill[FileImportAction.FIREDEVICE_COLUMN_ID2] + "-" +
                pill[FileImportAction.FIREDEVICE_COLUMN_ID3] + "-" +
                pill[FileImportAction.FIREEVICE_COLUMN_ID4];
        return  deviceNo;
    }
*/

    /*private FireDeviceDto getFireDeviceDto(String[] pill, int tableType){
        String deviceNo = getDeviceNo(pill);
        FireDeviceDto fireDeviceDto = new FireDeviceDto();
        if(FileImportAction.FIREDEVICE_POLYM_DATA == tableType){

            fireDeviceDto = new FireDeviceDto(null,null,null,null,deviceNo,pill[FileImportAction.FIREDEVICE_COLUMN_GATEWAY] );

        }else {
            fireDeviceDto = new FireDeviceDto(pill[FileImportAction.FIREDEVICE_COLUMN_ZONE].trim(),
                    pill[FileImportAction.FIREDEVICE_COLUMN_POINT].trim(),
                    pill[FileImportAction.FIREDEVICE_COLUMN_NETWORK].trim(),
                    pill[FileImportAction.FIREDEVICE_COLUMN_LOOP].trim(),
                    deviceNo,
                    pill[FileImportAction.FIREDEVICE_COLUMN_GATEWAY]);
        }
        return  fireDeviceDto;
    }*/

/*    // 提取设备
    private FireBusinessDeviceDto getFireBusinessDeviceDto(String[] pills){
        FireBusinessDeviceDto fireBusinessDeviceDto = new FireBusinessDeviceDto();
        fireBusinessDeviceDto.setDeviceNo(getDeviceNo(pills));

        // 地址
        String locationName = pills[FileImportAction.FIREDEVICE_COLUMN_BUILDING];
        String floor = pills[FileImportAction.FIREDEVICE_COLUMN_FLOOR];
        if(!StringUtils.isEmpty(floor)){
            locationName+="/"+floor;
        }

        List<Location> locationList = locationRepository.findByFullName(locationName);
        if(!locationList.isEmpty()){
            fireBusinessDeviceDto.setLocationId(locationList.get(0).getId());
        }

        fireBusinessDeviceDto.setDeviceLabel(pills[FileImportAction.FIREDEVICE_COLUMN_TAG].trim());

        //系统类型
        List<SystemType> systemTypeList = systemTypeRepository.findByName(FileImportAction.FIRE_SYSTEM_NAME);
        if(!systemTypeList.isEmpty()){
            fireBusinessDeviceDto.setSystemType(systemTypeList.get(0));
        }

        //设备类型
        List<DeviceType> deviceTypes = deviceTypeRepository.findByName(pills[FileImportAction.FIREDEVICE_COLUMN_DEVICETYPE]);
        if(!deviceTypes.isEmpty()){
            fireBusinessDeviceDto.setDeviceType(deviceTypes.get(0));
        }

        return  fireBusinessDeviceDto;
    }*/
    private String getMTpointCName(int k) {
        int t = k % 3;
        String name = "";
        switch (t) {
            case FireStatusConstant.ALARM_INDEX:
                name = FireStatusConstant.ALARM;
                break;
            case FireStatusConstant.INSULATE_INDEX:
                name = FireStatusConstant.INSULATE;
                break;
            case FireStatusConstant.TROUBLE_INDEX:
                name = FireStatusConstant.TROUBLE;
                break;
            default:
                break;

        }
        return name;
    }

    //提取point信息
   /* private  List<MTPoint> getMTPointList(String[] pills, int tableType){
        List<MTPoint> mtPoints = new ArrayList<>();
        int registerNo = Integer.valueOf(pills[FileImportAction.FIREDEVICE_COLUMN_RESIGTERNO]);//寄存器号
        String deviceType = pills[FileImportAction.FIREDEVICE_COLUMN_DEVICETYPE].trim();//设备类型
        MTPoint mtPoint = new MTPoint();
        mtPoint.setAddress(registerNo);
        List<FireDevice> fireDevices = fireDeviceRepository.findByBusinessDeviceNo(getDeviceNo(pills));
        if(!fireDevices.isEmpty()){
            mtPoint.setFireDevice(fireDevices.get(0));
        }

        int k = 0;
        for(int i = FileImportAction.FIREDEVICE_COLUMN_B0;i >= FileImportAction.FIREDEVICE_COLUMN_B15; i--){
            if(pills[i].equals("1")){
                MTPoint mtPoint1 = new MTPoint();
                BeanUtils.copyProperties(mtPoint, mtPoint1);
                mtPoint1.setKey(k);

                //多态设备
                if(FileImportAction.FIREDEVICE_POLYM_DATA == tableType){
                    if((deviceType.equals(FireStatusConstant.LOOP_NAME)) || deviceType.equals(FireStatusConstant.CONTROL_NAME)){
                        mtPoint1.setCName(getMTpointName( k,deviceType, registerNo));

                    }else{
                        mtPoint1.setCName(pills[FileImportAction.FIREDEVICE_COLUMN_EXTENDTYPE]);
                    }

                }
                else {
                    mtPoint1.setCName(getMTpointCName(k));
                }

                mtPoints.add(mtPoint1);
            }
            k++;
        }

        return  mtPoints;
    }
*/
    public String getMTpointName(int k, String deviceType, int registerNo) {
        String pointName = "";

        if ((deviceType.equals(FireStatusConstant.CONTROL_NAME)) && (FireStatusConstant.CONTROL_INDEXONE == (registerNo % 10)) && (k <= 5)) {
            pointName = FireStatusConstant.CONTROLONE_MAP.get(k + "");
        } else if ((deviceType.equals(FireStatusConstant.CONTROL_NAME)) && (FireStatusConstant.CONTROL_INDEXTWO == (registerNo % 10))) {
            pointName = FireStatusConstant.CONTROLTWO_MAP.get(k + "");
        } else {
            pointName = FireStatusConstant.LOOP_MAP.get(k + "");
        }

        return pointName;
    }


    public void checkAndSaveFireBuinessDevice(FireBusinessDeviceDto fireBusinessDeviceDto) {
        List<BusinessDevice> businessDevices = businessDeviceRepository.findByDeviceNo(fireBusinessDeviceDto.getDeviceNo());
        //判断系统中是否存在该设备
        if (businessDevices.isEmpty()) {
            //添加设备
            BusinessDevice businessDevice = new BusinessDevice();
            BeanUtils.copyProperties(fireBusinessDeviceDto, businessDevice);
            businessDeviceRepository.save(businessDevice);

        } else {
            //更新
            LOGGER.info("checkAndSaveFireBuinessDevice device already existed: " + fireBusinessDeviceDto.getDeviceNo());
            BusinessDevice device = businessDeviceRepository.getOne(businessDevices.get(0).getId());
            BeanUtils.copyProperties(fireBusinessDeviceDto, device);
            businessDeviceRepository.save(device);
        }

    }

    public void checkAndSaveMTPoint(List<MTPoint> mtPoints) {
        if (mtPoints.isEmpty()) {
            return;
        }

        for (MTPoint mtPoint : mtPoints) {
            mtPointRepository.save(mtPoint);
        }
    }

    private void checkAndDeleteFireBuinessDevice(FireBusinessDeviceDto fireBusinessDeviceDto) {

        List<BusinessDevice> businessDevices = businessDeviceRepository.findByDeviceNo(fireBusinessDeviceDto.getDeviceNo());
        //判断系统中是否存在该设备
        if (!businessDevices.isEmpty()) {
            businessDeviceRepository.deleteById(businessDevices.get(0).getId());
        }

    }

    private void checkAndDeleteMTPoints(FireDeviceDto fireDeviceDto) {
        List<FireDevice> fireDeviceList = fireDeviceRepository.findByBusinessDeviceNo(fireDeviceDto.getBusinessDeviceNo());
        if (!fireDeviceList.isEmpty()) {
            FireDevice fireDevice = fireDeviceList.get(0);
            Long deviceId = fireDevice.getId();
            List<MTPoint> mtPoints = mtPointRepository.findByFireDevice_Id(deviceId);
            if (mtPoints.isEmpty()) {
                LOGGER.error("checkAndDeleteMTPoints mtPoints.isEmpty()");
                return;
            }

            for (MTPoint mtPoint : mtPoints) {
                mtPointRepository.delete(mtPoint);
            }
        }


    }

   /* private void checkAndDeleteData(String line, int columnNum, int tableType ) {
        LOGGER.info("call checkAndDeleteData");
        String[] pills = line.split(",", columnNum);
        FireDeviceDto fireDeviceDto = getFireDeviceDto(pills, tableType);

        // 如果存在则
        //  1、删除business
        checkAndDeleteFireBuinessDevice(getFireBusinessDeviceDto(pills));

        //  2、删除 对应point （根据设备id）
        checkAndDeleteMTPoints(fireDeviceDto);

         //  3、删除设备
        checkAndDeleteDevice(fireDeviceDto);

    }*/




   /* private void checkAndSaveData(String line, int columnNum, int tableType) {
        LOGGER.info("call checkAndSaveData");
        String[] pills = line.split(",", columnNum);
        String deviceNo = getDeviceNo(pills);
        //T 保存或者更新businessDevice
        FireBusinessDeviceDto fireBusinessDeviceDto = getFireBusinessDeviceDto(pills);
        checkAndSaveFireBuinessDevice(fireBusinessDeviceDto);


        // 保存或者更新fireDevice
        FireDeviceDto fireDeviceDto = getFireDeviceDto(pills, tableType);
        checkAndSaveDevice(fireDeviceDto);

        // 保存或者更新多个point
        List<MTPoint>  mtPointList = getMTPointList(pills, tableType);
        checkAndSaveMTPoint(mtPointList);


    }*/

    private void checkAndDeleteDevice(FireDeviceDto deviceDto) {
        List<FireDevice> fireDeviceList = fireDeviceRepository.findByBusinessDeviceNo(deviceDto.getBusinessDeviceNo());
        //判断系统中是否存在该设备
        if (!fireDeviceList.isEmpty()) {
            //删除
            fireDeviceRepository.deleteById(fireDeviceList.get(0).getId());
        }
    }


   /* public int operateLineData(int tableType, int operateType, String line, int columnNum, List<DevicePointDto> devicePointDtoList){
        int result = FileImportAction.ERR_SUCCESS;

        //用于生成Json 文件
        if(FileImportAction.GENERATE_JSON == operateType ){

            DevicePointDto devicePointDto = getDevicePoinDo(line,columnNum);
            if(null == devicePointDto){
                result = FileImportAction.ERR_CLUMN;
            }else{
                devicePointDtoList.add(devicePointDto);

            }
        }
        else {
            result = operateDeviceDTO(line, operateType,columnNum, tableType);
        }

        return  result;
    }*/

   /* public int operateLinePolymData(int operateType, String line, int columnNum, List<DevicePointDto> devicePointDtoList){
        int result = 0;
        //多态数据操作, 从已经存在于数据库的数据中查找
        String[] pills = line.split(",", columnNum);
        String deviceNo = getDeviceNo(pills);
        DevicePointDto devicePointDto = findDevicePointDtoByNo( deviceNo);
        devicePointDtoList.add(devicePointDto);
        return  result;
    }*/

 /*   @Override
    public int[] readCsvAnOperateDB(MultipartFile file,ReadFileConditon readFileConditon) throws Exception {
        int[] result = new int[3];
        result[0] = FileImportAction.ERR_SUCCESS;
        int successRows = 0;

        List<String> stringList = CsvUtil.readCsvFile(file,readFileConditon);

        List<DevicePointDto> devicePointDtoList = new ArrayList<>();
        //4、将数据同步到数据库中
        for(String line: stringList) {
            result[0] = operateLineData( readFileConditon.getTableType(),readFileConditon.getOperateType(),  line,  readFileConditon.getColumnNum(), devicePointDtoList);
            if(result[0] != FileImportAction.ERR_SUCCESS){

                break;
            }
            successRows++;

        }

        if(!devicePointDtoList.isEmpty()) {
            FileUtil.writeJsonToFile(devicePointDtoList, FileImportAction.JSON_KEY, FileImportAction.JSON_DIR, readFileConditon.getFileName() + ".json");
        }


        result[1] = successRows;

        return result;
    }*/

  /*  private DevicePointDto getDevicePoinDo(String line, int columnNum) {
        String[] pills = line.split(",", columnNum);
        String deviceNo = getDeviceNo(pills);

        DevicePointDto devicePointDto = new DevicePointDto(deviceNo,pills[FileImportAction.FIREDEVICE_COLUMN_ZONE].trim(),
                pills[FileImportAction.FIREDEVICE_COLUMN_POINT].trim(), pills[FileImportAction.FIREDEVICE_COLUMN_TAG].trim(),
                pills[FileImportAction.FIREDEVICE_COLUMN_LOOP].trim(), pills[FileImportAction.FIREDEVICE_COLUMN_DEVICETYPE].trim(),
                pills[FileImportAction.FIREDEVICE_COLUMN_NETWORK].trim());
        return  devicePointDto;
    }*/

    @Override
    public int[] readExcelAndOperateDB(Workbook workbook, ReadFileConditon readFileConditon) throws Exception {
        int[] result = new int[3];
        LOGGER.info("call readExcelAndOperateDB");
        //2、将数据同步到数据库中
        int sheetNos = workbook.getNumberOfSheets();
        List<String> singleStateTypes = FileImportAction.SINGLET_STATE_TYPE_NAMES;
        Map<String, FireDevice> allDeviceNoAndFireDeviceMapping = new HashMap<>();
        Map<String, BusinessDevice> businessDeviceMap = new HashMap<>();
        Map<String, String> errorMsg = new HashMap<>();
        List<MTGateway> gatewayList = new ArrayList<>();
        for (int i = 0; i < sheetNos; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            if (sheetName.equals(FileImportAction.ALLDEVICESHEET)) {
                //原始表
                dealSheet(FileImportAction.IGONREROWS_ALLDEVICE, FileImportAction.CLUMNNUM_ALLDEVICE, sheet, allDeviceNoAndFireDeviceMapping, businessDeviceMap, gatewayList, errorMsg, FileImportAction.SHEET_TYPE_ALLDEVICE);
            } else if (sheetName.equals(FileImportAction.GATEWAY)) {
                //网关
                dealSheet(0, 1, sheet, allDeviceNoAndFireDeviceMapping, businessDeviceMap, gatewayList, errorMsg, FileImportAction.SHEET_TYPE_GATEWAY);
            } else {
                //单态设备表
                List<DeviceType> deviceTypes = deviceTypeRepository.findByName(sheetName);
                if (!deviceTypes.isEmpty()) {
                    DeviceType deviceType = deviceTypes.get(0);
                    if (deviceType.getAlarm()) {
                        dealSheet(FileImportAction.IGONREROWS_SINGLET, FileImportAction.CLUMNNUM__SINGLET, sheet, allDeviceNoAndFireDeviceMapping, businessDeviceMap, gatewayList, errorMsg, FileImportAction.SHEET_TYPE_SINGLET);
                    }
                }
                //多态设备表
            }
        }
        LOGGER.info("errorMsgs : " + errorMsg.toString());
        saveData(allDeviceNoAndFireDeviceMapping, businessDeviceMap, errorMsg);
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        result[0] = FileImportAction.ERR_SUCCESS;
        result[1] = businessDeviceMap.size() - errorMsg.size();

        return result;
    }

    private void saveData(Map<String, FireDevice> allDeviceNoAndFireDeviceMapping, Map<String, BusinessDevice> businessDeviceMap, Map<String, String> errorMsg) {
        errorMsg.clear();
        Map<String, List<DevicePointDto>> locationPointMap = new HashMap<>();
        Set<Map.Entry<String, BusinessDevice>> businessDeviceSet = businessDeviceMap.entrySet();
        for (Map.Entry<String, BusinessDevice> item : businessDeviceSet) {
            BusinessDevice businessDevice = item.getValue();
            String locationDetail = businessDevice.getLocationDetail();
            if (locationDetail != null) {
                locationDetail = locationDetail.replace("/", "-");
            } else {
                locationDetail = "-";
            }
            List<DevicePointDto> pointDtoList = locationPointMap.get(locationDetail);
            if (pointDtoList == null) {
                pointDtoList = new ArrayList<>();
                locationPointMap.put(locationDetail, pointDtoList);
            }
            FireDevice fireDevice = allDeviceNoAndFireDeviceMapping.get(businessDevice.getDeviceNo());
            if (fireDevice == null) {
                errorMsg.put(businessDevice.getDeviceNo(), "FireDevice IS NOT EXIST  ");
                continue;
            }
            DeviceType deviceType = businessDevice.getDeviceType();
            List<EventType> eventTypes = deviceType.getEventTypes();
            for (EventType eventType : eventTypes) {
                for (MTPoint mtPoint : fireDevice.getMtPoints()) {
                    if (mtPoint.getKey() == eventType.getKey()) {
                        mtPoint.setCName(eventType.getName());
                    }
                }
            }
            fireDeviceRepository.save(fireDevice);
            businessDeviceRepository.save(businessDevice);
            DevicePointDto devicePointDto = new DevicePointDto(fireDevice.getBusinessDeviceNo(), fireDevice.getZone(), fireDevice.getPoint(),
                    businessDevice.getDeviceLabel(), fireDevice.getLoop(), businessDevice.getDeviceType().getName(), fireDevice.getNetwork());
            pointDtoList.add(devicePointDto);
        }
        LOGGER.info("saveData errorMsgs : " + errorMsg.toString());
        if (!locationPointMap.isEmpty()) {
            Set<Map.Entry<String, List<DevicePointDto>>> entrySet = locationPointMap.entrySet();
            for (Map.Entry<String, List<DevicePointDto>> item : entrySet) {
                FileUtil.writeJsonToFile(item.getValue(), FileImportAction.JSON_KEY, FileImportAction.JSON_DIR, item.getKey() + ".json");
            }

        }
    }

    private void deleteData(Map<String, FireDevice> allDeviceNoAndFireDeviceMapping, List<BusinessDevice> businessDeviceList) {
        for (BusinessDevice businessDevice : businessDeviceList) {
            FireDevice fireDevice = allDeviceNoAndFireDeviceMapping.get(businessDevice.getDeviceNo());


        }
    }

    private void dealSheet(int ignoreRowIndex, int columnNum, Sheet sheet, Map<String, FireDevice> allDeviceNoAndFireDeviceMapping, Map<String, BusinessDevice> businessDeviceMap, List<MTGateway> gatewayList, Map<String, String> errorMsg, int type) {
        int rowNum = sheet.getLastRowNum() + 1;
        LOGGER.info("sheetName" + sheet.getSheetName());
        for (int j = ignoreRowIndex; j < rowNum; j++) {
            Row row = sheet.getRow(j);
//            LOGGER.info("read row["+ j+"]");
            if (null == row || (row.getFirstCellNum() < 0)) {
                LOGGER.error("row is empty or null");
                continue;
            }
            //读取每一行
            String line = ExcelUtil.readRowString(row, columnNum);
            operateLineData(line, allDeviceNoAndFireDeviceMapping, businessDeviceMap, gatewayList, errorMsg, type);

        }
    }


    private void operateLineData(String line, Map<String, FireDevice> allDeviceNoAndFireDeviceMapping, Map<String, BusinessDevice> businessDeviceMap, List<MTGateway> gatewayList, Map<String, String> errorMsg, int type) {

        String[] pills = line.split(",");
        //  LOGGER.info("read pills["+ pills.length+"]");
        if (type == FileImportAction.SHEET_TYPE_ALLDEVICE) {

            //原始表数据
            getFireDevice(pills, allDeviceNoAndFireDeviceMapping, gatewayList);

        } else if (type == FileImportAction.SHEET_TYPE_SINGLET) {
            //单态设备表数据
//            LOGGER.info("getBusinessDevice");
            BusinessDevice businessDevice = getBusinessDevice(pills, errorMsg);
            if (businessDevice != null && !StringUtils.isEmpty(businessDevice.getDeviceNo())) {
                businessDeviceMap.put(businessDevice.getDeviceNo(), businessDevice);
            }
        } else if (type == FileImportAction.SHEET_TYPE_GATEWAY) {
            String gatewayIP = pills[0];
            MTGateway mtGateway = mtGatewayRepository.findFirstByIp(gatewayIP);
            if (mtGateway != null) {
                gatewayList.add(mtGateway);
            }
        } else {
            // 多态设备表数据
        }
    }

    private BusinessDevice getBusinessDevice(String[] pills, Map<String, String> errorMsg) {

        BusinessDevice businessDevice = new BusinessDevice();
//        if(!StringUtils.isEmpty(pills[FileImportAction.DEVICE_COLUMN_ID].trim())){
//            businessDevice.setId(Long.valueOf(pills[FileImportAction.DEVICE_COLUMN_ID]));
//        }
        if (!StringUtils.isEmpty(pills[FileImportAction.DEVICE_COLUMN_DEVICENO].trim())) {
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
        if (!StringUtils.isEmpty(area)) {
            locationName += "/" + area;
        }
        if (!StringUtils.isEmpty(building)) {
            locationName += "/" + building;
        }
        if (!StringUtils.isEmpty(floor)) {
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
        List<DeviceType> deviceTypes = deviceTypeRepository.findByName(pills[FileImportAction.DEVICE_COLUMN_DEVICETYPE].trim());
        if (!deviceTypes.isEmpty()) {
            businessDevice.setDeviceType(deviceTypes.get(0));
        } else {
            errorMsg.put(pills[FileImportAction.DEVICE_COLUMN_DEVICENO].trim(), pills[FileImportAction.DEVICE_COLUMN_DEVICETYPE] + "IS NOT EXIST ");
            businessDevice = null;
        }
        return businessDevice;
    }

    private void getFireDevice(String[] pills, Map<String, FireDevice> allDeviceNoAndFireDeviceMapping, List<MTGateway> gatewayList) {
        if (!pills[FileImportAction.FIREDEVICE_COLUMN_ERRORFLAG].trim().isEmpty()) {
            LOGGER.error(pills[FileImportAction.FIREDEVICE_COLUMN_DEVICENO] + "error Line ");
            return;
        }
        FireDevice fireDevice = new FireDevice();
        fireDevice.setBusinessDeviceNo(pills[FileImportAction.FIREDEVICE_COLUMN_DEVICENO]);
        fireDevice.setNetwork(pills[FileImportAction.FIREDEVICE_COLUMN_NETWORK]);
        fireDevice.setZone(pills[FileImportAction.FIREDEVICE_COLUMN_ZONE]);
        fireDevice.setPoint(pills[FileImportAction.FIREDEVICE_COLUMN_POINT]);
        fireDevice.setLoop(pills[FileImportAction.FIREDEVICE_COLUMN_LOOP]);
        if (gatewayList.size() > 0) {
            fireDevice.setMtGateway(gatewayList.get(0));
        }
        List<MTPoint> mtPoints = new ArrayList<>();
        int k = 0;
        for (int i = FileImportAction.FIREDEVICE_COLUMN_B0; i >= FileImportAction.FIREDEVICE_COLUMN_B15; i--) {
            if ("1".equals(pills[i])) {
                MTPoint mtPoint = new MTPoint();
                mtPoint.setKey(k);
                mtPoint.setAddress(Integer.valueOf(pills[FileImportAction.FIREDEVICE_COLUMN_RESIGTERNO]));
                mtPoint.setFireDevice(fireDevice);
                mtPoints.add(mtPoint);
            }
            k++;
        }
        fireDevice.setMtPoints(mtPoints);

        allDeviceNoAndFireDeviceMapping.put(pills[FileImportAction.FIREDEVICE_COLUMN_DEVICENO], fireDevice);

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public int[] readFileAndSaveData(MultipartFile file, ReadFileConditon readFileConditon) throws Exception {

        int[] result = new int[3];

        //1、文件名字操作
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
            } else if (fileName.endsWith(FileImportAction.SUFFIX_CSV)) {
                // csv处理格式
//                return readCsvAnOperateDB(file,readFileConditon);
            } else {
                result[0] = FileImportAction.ERR_FILE;
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //excel处理
        return readExcelAndOperateDB(workbook, readFileConditon);
    }

    @Override
    public int[] readFileAndDeleteData(MultipartFile file, ReadFileConditon readFileConditon) throws Exception {
        return new int[0];
    }

    @Override
    public List<DevicePointDto> findDevicePoint(FireDevicePointSearch fireDevicePointSearch) {
        String locationFullName = fireDevicePointSearch.getBuilding();
        if (!StringUtils.isEmpty(fireDevicePointSearch.getFloor())) {
            locationFullName += "/" + fireDevicePointSearch.getFloor();
        }
        String fileName = locationFullName.replace("/", "-") + ".txt";
        List<Location> locationList = locationRepository.findByFullName(locationFullName);

        if (locationList.isEmpty()) {
            return null;
        }

        Long locationId = locationList.get(0).getId();
        List<BusinessDevice> businessDevices = businessDeviceRepository.findByLocationIdAndSystemType_Name(locationId, fireDevicePointSearch.getSystemType());
        if (businessDevices.isEmpty()) {
            return null;
        }

        List<DevicePointDto> devicePointDtoList = new ArrayList<>();
        for (BusinessDevice businessDevice : businessDevices) {
            List<FireDevice> fireDeviceList = fireDeviceRepository.findByBusinessDeviceNo(businessDevice.getDeviceNo());
            if (!fireDeviceList.isEmpty()) {
                FireDevice fireDevice = fireDeviceList.get(0);
                DevicePointDto devicePointDto = new DevicePointDto(fireDevice.getBusinessDeviceNo(), fireDevice.getZone(), fireDevice.getPoint(),
                        businessDevice.getDeviceLabel(), fireDevice.getLoop(), businessDevice.getDeviceType().getName(), fireDevice.getNetwork());
                devicePointDtoList.add(devicePointDto);
            }
        }

        if ((!devicePointDtoList.isEmpty()) && (devicePointDtoList != null)) {
            FileUtil.writeJsonToFile(devicePointDtoList, null, FileImportAction.JSON_DIR, fileName);
        }
        return devicePointDtoList;
    }

    @Override
    public List<ValueDto> findFireDeviceCount(WaterDeviceSearch search) {
        // 获取水系统设备数量
        Map<String, Object> deviceCount = businessDeviceService.getDeviceCount("报警", search.getBuilding(), search.getFloor(), search.getDeviceType());
        List<ValueDto> deviceInfoDtos = new ArrayList<>();
        if (deviceCount != null) {
            deviceCount.forEach((key, value) -> {
                ValueDto info = new ValueDto();
                info.setName(key);
                info.setValue(value);
                deviceInfoDtos.add(info);
            });

        }
        return deviceInfoDtos;
    }


    @Override
    public List<FireDevice> findByBusinessDeviceNo(String businessDeviceNo) {
        return fireDeviceRepository.findByBusinessDeviceNo(businessDeviceNo);
    }

    @Override
    public FireDeviceDto toDto(FireDevice fireDevice) {
        FireDeviceDto fireDeviceDto = new FireDeviceDto();
        BeanUtils.copyProperties(fireDevice, fireDeviceDto);
        fireDeviceDto.setGatewayNo(fireDevice.getMtGateway().getId() + "");
        fireDeviceDto.setStatuss(fireDevice.getStatus());
        return fireDeviceDto;
    }

    @Override
    public FireDevice findFirstByDeviceId(String businessDeviceNo) {
        List<FireDevice> list = fireDeviceRepository.findByBusinessDeviceNo(businessDeviceNo);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

//    ------------------------------------------第二版----------------------------------------------------------------------


    @Override
    public Pagination findPage2BFL() {
        Page<BusinessDevice> pageList = businessDeviceRepository.findAll(JpaUtils.getSpecification(), JpaUtils.getPageRequest());
        List<BusinessFireDeviceDto> bfdd = new ArrayList<>(10);
        pageList.forEach(businessDevice -> {
            bfdd.add(businessDeviceService.toBFDDto(businessDevice));
        });
        return new Pagination((int) pageList.getTotalElements(), bfdd);
    }

    public List<BusinessFireDeviceDto> find(Specification<BusinessDevice> specification) {
        List<BusinessDevice> businessDeviceList = businessDeviceRepository.findAll(specification);
        List<BusinessFireDeviceDto> dtoList = new ArrayList<>();
        for (BusinessDevice businessDevice : businessDeviceList) {
            dtoList.add(businessDeviceService.toBFDDto(businessDevice));
        }
        return dtoList;
    }


    @Override
    public List<Map<String, Object>> countDeviceByStatus() {
        Map<Object, Object> map = new HashMap<>(2);
        List<Map<String, Object>> deviceStatusCountMap = fireDeviceRepository.getDeviceStatusCount();

        for (Map<String, Object> countMap : deviceStatusCountMap) {
            if (StringUtils.isEmpty(countMap.get("name"))) {
                map.put("正常", countMap.get("value"));
            } else {
                map.put(countMap.get("name"), countMap.get("value"));
            }
        };
        return MapUtil.map2Listmap(map);
    }

    @Override
    public List<Map<String, Object>> countDeviceByDeviceType() {
        List<Map<String, Object>> resultList = new ArrayList<>(6);
        List<Map<String, Object>> lists = businessDeviceRepository.getDeviceCount("%" + SystemTypeRepository.FIRESYSTEM + "%");
//        Long allCount = 0L;
//        Long someCount = 0L;
        for (Map<String, Object> map : lists) {
//            allCount += (long) map.get("value");
//            if (map.containsValue(DeviceTypeRepository.PAI_YAN_FA) || map.containsValue(DeviceTypeRepository.SHOU_BAO) || map.containsValue(DeviceTypeRepository.WEI_ZHI)
//                    || map.containsValue(DeviceTypeRepository.XIAO_HUO_SHUANG) || map.containsValue(DeviceTypeRepository.YAN_GAN)) {
//                someCount += (long) map.get("value");
                resultList.add(map);
        }
//        Map<String, Object> otherMap = new HashMap<>(2);
//        otherMap.put("name", DeviceTypeRepository.OTHER);
//        otherMap.put("value", allCount - someCount);
        return resultList;
    }

    //仅用于已入库数据的修改报警名称
    public Integer patchChangeMTPointCName() {
        List<DeviceType> types = deviceTypeRepository.findByEventTypeName("火警");
        List<BusinessDevice> devices = businessDeviceRepository.findByDeviceTypeIn(types);
        int i = 0;
        for (BusinessDevice businessDevice : devices) {
            FireDevice fireDevice = fireDeviceRepository.findFirstByBusinessDeviceNo(businessDevice.getDeviceNo());
            if (fireDevice != null && fireDevice.getMtPoints() != null) {
                for (MTPoint mtPoint : fireDevice.getMtPoints()) {
                    if (mtPoint.getKey() == 1 && mtPoint.getCName().equals("报警")) {
                        mtPoint.setCName("火警");
                        i++;
                    }
                }
                fireDeviceRepository.save(fireDevice);

            }
        }
        return i;
    }
}
