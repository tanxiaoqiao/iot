package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.EnableTraceLog;
import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.ExcelTitle;
import com.honeywell.fireiot.constant.FileImportAction;
import com.honeywell.fireiot.constant.TraceLogType;
import com.honeywell.fireiot.dto.BusinessDeviceDto;
import com.honeywell.fireiot.dto.BusinessDeviceSearch;
import com.honeywell.fireiot.dto.BusinessLocationDto;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.DeviceTypeService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.service.SystemTypeService;
import com.honeywell.fireiot.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 设备台账管理
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/21 1:54 PM
 */
@RestController
@RequestMapping("/api/businessDevice")
@Slf4j
@Api(tags = "设备台账")
public class BusinessDeviceController {
    @Autowired
    BusinessDeviceService businessDeviceService;
    @Autowired
    SystemTypeService systemTypeService;
    @Autowired
    DeviceTypeService deviceTypeService;
    @Autowired
    LocationService locationService;

    @GetMapping
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<BusinessDeviceDto>> findPage() {
//        Page<BusinessDevice> pageList = businessDeviceService.findPage(JpaUtils.getSpecification());
//        Pagination<BusinessDevice> pagination = new Pagination<BusinessDevice>((int) pageList.getTotalElements(), pageList.getContent());
//        return ResponseObject.success(pagination);

        Pagination<BusinessDeviceDto> pagination  = businessDeviceService.findPage(JpaUtils.getSpecification());
        return ResponseObject.success(pagination);
    }


    @GetMapping("/excel")
    @JpaPage
    @ApiOperation(value = "按条件查询，不分页，导出Excel使用")
    public ResponseObject find(HttpServletResponse response) {
       List<BusinessDeviceDto> results  = businessDeviceService.find(JpaUtils.getSpecification());
       if(results.size()==0){
           return ResponseObject.fail(ErrorEnum.DEVICE_NOT_EXIST);
       }
        String fileName = UUID.randomUUID() + ".xlsx";
        //构造excelData
        ExcelData data = new ExcelData();
        data.setFileName(fileName);
        data.setTitle(ExcelTitle.DEVICE_TITLE_LIST);
        List<List<Object>> rowData = new ArrayList<>();
       for(BusinessDeviceDto dto :results){
           List<Object> row = new ArrayList<>();
           row.add(dto.getId());
           row.add(dto.getDeviceNo());
           row.add(dto.getDeviceLabel());
           row.add(dto.getDeviceType()==null?"":dto.getDeviceType().getName());
           row.add(dto.getLocationDetail());
           row.add(dto.getBrandName());
           row.add(dto.getDateOfPoduction());
           row.add(dto.getDateOfInstallation());
           row.add(dto.getDateOfCommissioning());
           row.add(dto.getLifeTime());
           row.add(dto.getDescription());
           rowData.add(row);
       }
       data.setRows(rowData);
       try {
           ExcelUtil.exportExcel(response, data);
       }catch(Exception e){
            e.printStackTrace();
       }
        return ResponseObject.success(null);
    }

    @PostMapping("/patchUpdate")
    @ApiOperation(value = "导入设备文件(csv,xls, xlsx)", notes = "批量更新设备") //方法描述
    public ResponseObject patchUpdate(@RequestParam("file") MultipartFile file,
                                        HttpServletRequest request) throws Exception {
        if(null == file){
            return  ResponseObject.fail(ErrorEnum.IMPORT_DATA_FAIL);
        }

        int[] result = businessDeviceService.readFileAndSaveData(file);
        return  FileUtil.getErrResult(result);
    }
    @ApiOperation(value = "通过ID查找")
    @GetMapping("/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
//        Optional<BusinessDevice> entity = businessDeviceService.findById(id);
//        if (entity.isPresent()) {
//            return ResponseObject.success(businessDeviceService.toDto(entity.get()));
//        }
//        return ResponseObject.fail(ErrorEnum.NOT_FOUND);

        BusinessDeviceDto result = businessDeviceService.findById(id);
        if (result!=null) {
            return ResponseObject.success(result);
        }else {
            return ResponseObject.fail(ErrorEnum.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation(value = "保存实体")
    @EnableTraceLog(type = TraceLogType.DEVICE, content = "设备新增：${log_deviceNo}")
    public ResponseObject save(@ApiParam @Validated @RequestBody BusinessDeviceDto dto, HttpServletRequest request) {
//        BusinessDevice entity = new BusinessDevice();
//        BeanUtils.copyProperties(dto, entity);
//        if (entity.getSystemType() != null && entity.getSystemType().getId() != null) {
//            entity.setSystemType(systemTypeService.findById(entity.getSystemType().getId()).get());
//        }
//        if (entity.getDeviceType() != null && entity.getDeviceType().getId() != null) {
//            entity.setDeviceType(deviceTypeService.findById(entity.getDeviceType().getId()).get());
//        }
//        businessDeviceService.save(entity);
//        return ResponseObject.success("OK");

        businessDeviceService.save(dto);
        // 注入log变量
        request.setAttribute("log_deviceNo", dto.getDeviceNo());
        return ResponseObject.success("OK");
    }

    @PatchMapping
    @ApiOperation(value = "部分更新")
    @EnableTraceLog(type = TraceLogType.DEVICE, content = "设备更新：${log_deviceNo}")
    public ResponseObject patchUpdate(@ApiParam @RequestBody BusinessDeviceDto dto, HttpServletRequest request) {
//        Optional<BusinessDevice> op = businessDeviceService.findById(dto.getId());
//        if (op.isPresent()) {
//            BusinessDevice entity = op.get();
//            BeanUtils.copyProperties(dto, entity);
//            if (dto.getSystemType() != null && dto.getSystemType().getId() != null
//                    && !dto.getSystemType().getId().equals(entity.getSystemType().getId())) {
//                entity.setSystemType(systemTypeService.findById(dto.getSystemType().getId()).get());
//            }
//            if (dto.getDeviceType() != null && dto.getDeviceType().getId() != null
//                    && !dto.getDeviceType().getId().equals(entity.getDeviceType().getId())) {
//                entity.setDeviceType(deviceTypeService.findById(dto.getDeviceType().getId()).get());
//            }
//            return ResponseObject.success("OK");
//        }
//        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);

        boolean isUpdated = businessDeviceService.update(dto);
        // 注入log变量
        request.setAttribute("log_deviceNo", dto.getDeviceNo());
        if (isUpdated) {
            return ResponseObject.success("OK");
        }

        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }

    @PatchMapping("/mapLocation")
    @ApiOperation(value = "根据deviceId更新mapLocation")
    public ResponseObject updateMapLocation(@RequestBody List<Map<String,String>> data) {

        try {
            for(Map<String,String> map : data){
                businessDeviceService.updateMapLocation(map.get("mapLocation"),map.get("deviceId"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResponseObject.success("OK");
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/{id}")
    @EnableTraceLog(type = TraceLogType.DEVICE, content = "设备删除：${log_deviceNo}")
    public ResponseObject delete(@PathVariable("id") Long id, HttpServletRequest request) {
        BusinessDeviceDto result = businessDeviceService.findById(id);
        businessDeviceService.deleteById(id);
        // 注入log变量
        request.setAttribute("log_deviceNo", result.getDeviceNo());
        return ResponseObject.success("OK");
    }

    @GetMapping("/system")
    @ApiOperation(value = "获取消防所有设备")
    public ResponseObject getBySystem(BusinessDeviceSearch businessDeviceSearch){
        PageSearch pageSearch = new PageSearch();
        pageSearch.setPi(businessDeviceSearch.getPi()-1);
        pageSearch.setPs(businessDeviceSearch.getPs());
        PageHolder.setHolder(pageSearch);
        Specification<BusinessDevice> specification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("systemType").get("name"),businessDeviceSearch.getSystemType());
        Pagination<BusinessLocationDto>  pages= businessDeviceService.getBusinessLocation(specification);
//        Page<BusinessDevice> page = businessDeviceService.getBusinessLocation(specification);
//        List<BusinessLocationDto> businessLocationDtoList = new ArrayList<>();
//        page.getContent().forEach(businessDevice ->
//                businessLocationDtoList.add(businessDeviceService.toBLDto(businessDevice))
//        );
//        Pagination pages = new Pagination( (int) page.getTotalElements(),businessLocationDtoList);
        return  ResponseObject.success(pages);
    }

    @GetMapping("/deviceId/{id}")
    @ApiOperation(value = "根据deviceId获取设备详情")
    public ResponseObject getByDeviceId(@PathVariable("id") String id){
       BusinessDevice businessDevice =  businessDeviceService.findByNo(id);
       if(businessDevice == null){
           // 设备不存在
           return ResponseObject.fail(ErrorEnum.DEVICE_NOT_EXIST);
       }else{
           return ResponseObject.success(businessDeviceService.toBLDto(businessDevice));
       }
    }


    @PostMapping("/uploadJson")
    @ApiOperation(value = "上传文件填充mapLocation字段")
    public ResponseObject upLoadDeviceJson(@RequestParam("file") MultipartFile file) {
        businessDeviceService.setMapLocationInfo( file);
        return ResponseObject.success(null);
    }


    @GetMapping("location/{id}")
    public ResponseObject getDeviceByLocationId(@PathVariable("id") Long id){
//        List<Long> ids = new ArrayList<>();
//        List<Location> locationList = locationService.getChildren(id);
//        while( locationList != null){
//            locationList.forEach(location -> ids.add(location.getId()));
//
//        }

        return ResponseObject.success(null);
    }
    @GetMapping("/qrCode")
    @JpaPage
    public ResponseObject createCRCode(){
        businessDeviceService.createQRCode();
        return ResponseObject.success(null);
    }

    @ApiOperation(value = "删除多个实体")
    @DeleteMapping("patchDelete/{id}")
    @EnableTraceLog(type = TraceLogType.DEVICE, content = "设备批量删除：${log_deviceIds}")
    public ResponseObject delete(@PathVariable("id") Long[] ids, HttpServletRequest request) {
        businessDeviceService.deleteById(ids);
        // 注入log变量
        request.setAttribute("log_deviceIds",ids);
        return ResponseObject.success("OK");
    }


    @PostMapping("/upload")
    @ApiOperation(value = "导入设备文件(xls, xlsx)", notes = "上传设备文件（前端导入的设备保存到数据库中）") //方法描述
    public ResponseObject upLoadDevices(@RequestParam("file") MultipartFile file,
                                        HttpServletRequest request) throws Exception {
        if(null == file){
            return  ResponseObject.fail(ErrorEnum.IMPORT_DATA_FAIL);
        }
        int[] result =businessDeviceService.uploadDeviceForPolling(file);
        return  FileUtil.getErrResult(result);
    }
}
