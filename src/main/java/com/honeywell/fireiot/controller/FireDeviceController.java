package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.ExcelTitle;
import com.honeywell.fireiot.constant.FileImportAction;
import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.dto.firedevice.DevicePointDto;
import com.honeywell.fireiot.dto.firedevice.FireDeviceDto;
import com.honeywell.fireiot.dto.firedevice.FireDevicePointSearch;
import com.honeywell.fireiot.dto.waterdevice.ValueDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceSearch;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.FireDevice;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.FireDeviceService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:29 PM 12/18/2018
 */
@RestController
@RequestMapping("/api/fireDevice")
@Api(value = "消防设备管理", tags = {"消防设备管理"})
@Slf4j
public class FireDeviceController {


    @Autowired
    FireDeviceService fireDeviceService;

    @Autowired
    BusinessDeviceService businessDeviceService;

    @Autowired
    LocationService locationService;

    @PostMapping
    @ApiOperation(value = "增加设备", notes = "增加设备") //方法描述
    public ResponseObject addDevice(@Validated @RequestBody FireDeviceDto deviceDto) {
        log.info("addDevice| deviceDto:{}", deviceDto);
        if (fireDeviceService.checkUniqueDevice(deviceDto)) {
            return ResponseObject.fail(ErrorEnum.DEVICE_EXIST);
        }

        fireDeviceService.add(deviceDto);
        return ResponseObject.success(deviceDto.getId());
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除设备") //方法描述
    public ResponseObject deleteDevice(@PathVariable("id") Long id) {
        log.info("deleteDevice|id:{} ", id);
        //id号是否存在
        if (null == fireDeviceService.findById(id)) {
            return ResponseObject.fail(ErrorEnum.DEVICE_NOT_EXIST);
        }
        fireDeviceService.deleteById(id);
        return ResponseObject.success(null);
    }


    @PutMapping
    @ApiOperation(value = "更新设备信息", notes = "更新区域信息id号为必传") //方法描述
    public ResponseObject updateDevice(@ApiParam @Validated @RequestBody FireDeviceDto deviceDto) {
        log.info("updateDevice|deviceDto:{} ", deviceDto);
        //id号是否存在
        FireDevice oldDevice = fireDeviceService.findById(deviceDto.getId());
        if (null == oldDevice) {
            return ResponseObject.fail(ErrorEnum.DEVICE_NOT_EXIST);
        }
        if (fireDeviceService.checkUpdateDevice(deviceDto)) {
            return ResponseObject.fail(ErrorEnum.DEVICE_EXIST);
        }
        FireDevice device = new FireDevice();
        fireDeviceService.toEntity(deviceDto, device);
        fireDeviceService.update(device);
        return ResponseObject.success(null);
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "通过Id查找设备") //方法描述
    public ResponseObject findDevice(@PathVariable("id") Long id) {
        log.info("findDevice|id:{} ", id);
        //id号是否存在
        FireDevice device = fireDeviceService.findById(id);
        if (null == device) {
            return ResponseObject.fail(ErrorEnum.DEVICE_NOT_EXIST);
        }
        return ResponseObject.success(device);
    }

    @ApiOperation(value = "查询点位信息并生成json文件")
    @PostMapping("/generateJson")
    public ResponseObject findDeviceCount(@RequestBody FireDevicePointSearch[] searchList) {

        for(FireDevicePointSearch fireDevicePointSearch: searchList){
          List<DevicePointDto> devicePointDtoList = fireDeviceService.findDevicePoint(fireDevicePointSearch);
            System.out.println("size="+ devicePointDtoList.size());
        }
        return ResponseObject.success(null);
    }

    @PostMapping("/upload/generateJson")
    @ApiOperation(value = "导入设备空间位置文件(xls, xlsx)", notes = "上传空间位置搜索的文件") //方法描述
    public ResponseObject generateJsonData(@RequestParam("file") MultipartFile file) throws Exception {
        if(null == file){
            return  ResponseObject.fail(ErrorEnum.IMPORT_DATA_FAIL);
        }

        ReadFileConditon readFileConditon = new ReadFileConditon();
        readFileConditon.setColumnNum(2);
        List<String>  stringList =  ExcelUtil.readExcel(file, readFileConditon);
        if(stringList.isEmpty()){
            return ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }

        for(String str: stringList){
            String[] pill = str.split(",", 2);
            FireDevicePointSearch fireDevicePointSearch = new FireDevicePointSearch();
            fireDevicePointSearch.setBuilding(pill[0]);
            fireDevicePointSearch.setFloor(pill[1]);
            fireDevicePointSearch.setSystemType(FileImportAction.FIRE_SYSTEM_NAME);
            fireDeviceService.findDevicePoint(fireDevicePointSearch);
        }
        return  ResponseObject.success(null);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "导入设备文件(csv,xls, xlsx)", notes = "上传设备文件（前端导入的设备保存到数据库中）") //方法描述
    public ResponseObject upLoadDevices(@RequestParam("file") MultipartFile file,
                                        HttpServletRequest request) throws Exception {
        if(null == file){
            return  ResponseObject.fail(ErrorEnum.IMPORT_DATA_FAIL);
        }
        ReadFileConditon readFileConditon = ReadFileConditon.getFireDevice();
        readFileConditon.setOperateType(FileImportAction.ADD_DBDATA);
        readFileConditon.setTableType(FileImportAction.FIREDEVICE_SINGLETON_DATA);
        readFileConditon.setSheetIndex(FileImportAction.FIREDEVICE_SINGLETON_DATA);
        int[] result = fireDeviceService.readFileAndSaveData(file,readFileConditon);
        return  FileUtil.getErrResult(result);
    }

    @PostMapping("/uploadDelDevice")
    @ApiOperation(value = "导入设备文件用于批量删除设备(csv,xls, xlsx)", notes = "上传设备文件（前端导入的设备）") //方法描述
    public ResponseObject upLoadDelDevices(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        if(null == file){
            return  ResponseObject.fail(ErrorEnum.IMPORT_DATA_FAIL);
        }
        ReadFileConditon readFileConditon = ReadFileConditon.getFireDevice();
        readFileConditon.setOperateType(FileImportAction.REMOVE_DBDATA);
        readFileConditon.setTableType(FileImportAction.FIREDEVICE_SINGLETON_DATA);
        readFileConditon.setSheetIndex(FileImportAction.FIREDEVICE_SINGLETON_DATA);
        int[] result = fireDeviceService.readFileAndDeleteData(file,readFileConditon);
        return  FileUtil.getErrResult(result);
    }

    @PostMapping("/uploadPolym")
    @ApiOperation(value = "导入多态设备文件(csv,xls, xlsx)", notes = "上传设备文件（前端导入的设备保存到数据库中）") //方法描述
    public ResponseObject upLoadPolymDevices(@RequestParam("file") MultipartFile file,
                                        HttpServletRequest request) throws Exception {
        if(null == file){
            return  ResponseObject.fail(ErrorEnum.IMPORT_DATA_FAIL);
        }
        ReadFileConditon readFileConditon = ReadFileConditon.getFireDevice();
        readFileConditon.setOperateType(FileImportAction.ADD_DBDATA);
        readFileConditon.setTableType(FileImportAction.FIREDEVICE_POLYM_DATA);
        readFileConditon.setSheetIndex(FileImportAction.FIREDEVICE_POLYM_SHEETINDEX);
        int[] result = fireDeviceService.readFileAndDeleteData(file,readFileConditon);
        return  FileUtil.getErrResult(result);
    }

   /* @PostMapping("/uploadDelPolymDevices")
    @ApiOperation(value = "导入多态设备文件用于批量删除设备(csv,xls, xlsx)", notes = "上传设备文件（前端导入的设备）") //方法描述
    public ResponseObject upLoadDelPolymDevices(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        if(null == file){
            return  ResponseObject.fail(ErrorEnum.IMPORT_DATA_FAIL);
        }
        ReadFileConditon readFileConditon = ReadFileConditon.getFireDevice();
        readFileConditon.setOperateType(FileImportAction.REMOVE_DBDATA);
        readFileConditon.setTableType(FileImportAction.FIREDEVICE_POLYM_DATA);
        readFileConditon.setSheetIndex(FileImportAction.FIREDEVICE_POLYM_SHEETINDEX);
        int[] result = fireDeviceService.readFileAndOperateDB(file,readFileConditon);
        return  FileUtil.getErrResult(result);
    }*/



    @ApiOperation(value = "查询消防系统设备数量(大屏)")
    @PostMapping("platform/count")
    public ResponseObject findDeviceCount(@RequestBody WaterDeviceSearch[] searchList) {
        List<FireBaseDto> infoDtoList = new ArrayList<>();
        // 循环查询所有的数量
        for (WaterDeviceSearch search : searchList) {
            FireBaseDto infoDto = new FireBaseDto();
            List<ValueDto> values = fireDeviceService.findFireDeviceCount(search);
            infoDto.setValues(values);
            infoDto.setSearch(search);
            infoDtoList.add(infoDto);
        }
        return ResponseObject.success(infoDtoList);
    }

    @PostMapping("platform/detail")
    @ApiOperation(value = "根据设备ID查询设备详情（大屏）")
    public ResponseObject getByDeviceId(@RequestBody String[] devices) {
        List<FireBaseDto> fireBaseDtos = null;
        try {
            fireBaseDtos = new ArrayList<>();
            for (String deviceId : devices) {
                FireBaseDto fireBaseDto = new FireBaseDto();
                Map searchMap = new HashMap();
                searchMap.put("deviceId", deviceId);
                fireBaseDto.setSearch(searchMap);
                BusinessDevice businessDevice = businessDeviceService.findByNo(deviceId);
                if(businessDevice != null){
                    BusinessLocationDto businessLocationDto = businessDeviceService.toBLDto(businessDevice);
                    FireDevice fireDevice = fireDeviceService.findFirstByDeviceId(deviceId);
                    if(fireDevice != null){
                        businessLocationDto.setDeviceEventStatus(fireDevice.getStatus());
                        businessLocationDto.setNetwork(fireDevice.getNetwork());
                        businessLocationDto.setLoop(fireDevice.getLoop());
                        businessLocationDto.setZone(fireDevice.getZone());
                        businessLocationDto.setPoint(fireDevice.getPoint());
                    }
                    fireBaseDto.addObj(businessLocationDto);
                }
                fireBaseDtos.add(fireBaseDto);
            }
            return ResponseObject.success(fireBaseDtos);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseObject.fail(ErrorEnum.SERVER_ERROR);
        }

    }

    @PostMapping("platform/locations")
    @ApiOperation(value = "根据楼栋楼层查询设备详情(大屏)")
    public ResponseObject getList(@RequestBody LocationSearch[] list) {
        try {
            List<FireDeviceInfo> fireDeviceInfoList = new ArrayList<>();
            for (LocationSearch search : list) {
                FireDeviceInfo fireDeviceInfo = new FireDeviceInfo();
                fireDeviceInfo.setSearch(search);
                List<BusinessDevice> businessDeviceList = businessDeviceService.getDevice("报警", search.getBuilding(), search.getFloor(), null);

                businessDeviceList.forEach(businessDevice -> {
                    DeviceEventDto deviceEventDto = new DeviceEventDto();
                    deviceEventDto.setDeviceId(businessDevice.getDeviceNo());
                    deviceEventDto.setDeviceLabel(businessDevice.getDeviceLabel());
                    deviceEventDto.setDeviceType(businessDevice.getDeviceType().getName());
                    deviceEventDto.setMapLocation(businessDevice.getMapLocation());
                    FireDevice fireDevice = fireDeviceService.findByBusinessDeviceNo(businessDevice.getDeviceNo()).get(0);
                    deviceEventDto.setDeviceEventStatus(fireDevice.getStatus());
                    fireDeviceInfo.addDeviceEventDto(deviceEventDto);
                });
                fireDeviceInfoList.add(fireDeviceInfo);
            }
            return ResponseObject.success(fireDeviceInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("服务器异常");
            return ResponseObject.fail(ErrorEnum.SERVER_ERROR);
        }
    }

    @GetMapping("/deviceId/{id}")
    @ApiOperation(value = "通过DeviceId查找设备") //方法描述
    public ResponseObject findDeviceByDeviceId(@PathVariable("id") String id) {
        //id号是否存在
        FireDevice device = fireDeviceService.findByBusinessDeviceNo(id).get(0);
        if (null == device) {
            return ResponseObject.fail(ErrorEnum.DEVICE_NOT_EXIST);
        }
        return ResponseObject.success(fireDeviceService.toDto(device));
    }



    @GetMapping("/list")
    @ApiOperation(value = "获取消防所有设备，分页") //方法描述
    @JpaPage
    public ResponseObject findFireDeviceList() {
        return ResponseObject.success(fireDeviceService.findPage2BFL());
    }

    @GetMapping("/excel")
    @ApiOperation(value = "导出消防所有设备，不分页") //方法描述
    @JpaPage
    public ResponseObject exportFireDeviceList(HttpServletResponse response) throws Exception{
      List<BusinessFireDeviceDto> results = fireDeviceService.find(JpaUtils.getSpecification());
        if(results.size()==0){
            return ResponseObject.fail(ErrorEnum.DEVICE_NOT_EXIST);
        }
        String fileName = UUID.randomUUID() + ".xlsx";
        //构造excelData
        ExcelData data = new ExcelData();
        data.setFileName(fileName);
        data.setTitle(ExcelTitle.FIRE_TITLE_LIST);
        List<List<Object>> rowData = new ArrayList<>();
        for(BusinessFireDeviceDto businessFireDeviceDto :results){
            List<Object> row = new ArrayList<>();
            row.add(businessFireDeviceDto.getId());
            row.add(businessFireDeviceDto.getDeviceNo());
            row.add(businessFireDeviceDto.getDeviceLabel());
            row.add(businessFireDeviceDto.getDeviceType());
            row.add(businessFireDeviceDto.getDeviceStatus()==null?"正常":businessFireDeviceDto.getDeviceStatus());
            row.add(businessFireDeviceDto.getBuilding());
            row.add(businessFireDeviceDto.getFloor());
            row.add(businessFireDeviceDto.getZone());
            row.add(businessFireDeviceDto.getPoint());
            rowData.add(row);
        }
        data.setRows(rowData);
        ExcelUtil.exportExcel(response,data);
        return ResponseObject.success(null);
    }


    @GetMapping("/count/status")
    @ApiOperation(value = "设备状态统计")
    public ResponseObject countDeviceStatusForHome() {
        return ResponseObject.success(fireDeviceService.countDeviceByStatus());
    }

    @GetMapping("/count/deviceType")
    @ApiOperation(value = "设备类型统计")
    public ResponseObject countDeviceForHome() {
        return ResponseObject.success(fireDeviceService.countDeviceByDeviceType());
    }

    @GetMapping("/ht/maplocation/{id}")
    @ApiOperation(value = "根据楼栋楼层查询设备（用于maplocation）")
    public ResponseObject getList(@PathVariable("id") Long id) {
        List<BusinessDevice> businessDeviceList = businessDeviceService.findByLocationId(id);
        if(businessDeviceList != null){
            List<MapLocationDto> list = businessDeviceList.stream().map(businessDevice -> {
                MapLocationDto mapLocationDto = new MapLocationDto();
                mapLocationDto.setId(businessDevice.getDeviceNo());
                mapLocationDto.setName(businessDevice.getDeviceLabel());
                mapLocationDto.setType(businessDevice.getDeviceType().getName());
                mapLocationDto.setMapLocation(businessDevice.getMapLocation());

                if (businessDevice.getSystemType()!=null && businessDevice.getSystemType().getName().contains("消防报警")) {
                    FireDevice fireDevice = fireDeviceService.findByBusinessDeviceNo(businessDevice.getDeviceNo()).get(0);
                    mapLocationDto.setLoop(fireDevice.getLoop());
                    mapLocationDto.setNetwork(fireDevice.getNetwork());
                    mapLocationDto.setPoint(fireDevice.getPoint());
                    mapLocationDto.setZone(fireDevice.getZone());
                }
                return  mapLocationDto;
            }).collect(toList());
            return ResponseObject.success(list);
        }
        return ResponseObject.success(null);
    }

    @PostMapping("/ht/maplocation/update")
    @ApiOperation(value = "根据ID更新设备maplocation")
    public ResponseObject updatemaplocation(@RequestBody List<MapLocationDto> list) {
        list.forEach(mapLocationDto -> businessDeviceService.updateMapLocation(mapLocationDto.getMapLocation(),mapLocationDto.getId()));
        return ResponseObject.success(null);
    }

    @PostMapping("/test/location")
    @ApiOperation(value = "修改location里消防系统所有楼栋绑定到楼层")
    public ResponseObject updateLocation() {
        businessDeviceService.updateLocationTest();
        return ResponseObject.success(null);
    }

    @PostMapping("/patchChangeMTPointCName")
    @ApiOperation(value = "修改location里消防系统所有楼栋绑定到楼层")
    public ResponseObject patchChangeMTPointCName() {
       Integer succNo =  fireDeviceService.patchChangeMTPointCName();
        return ResponseObject.success(succNo);
    }
}
