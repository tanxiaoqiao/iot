package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.DeviceTypeDto;
import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.service.DeviceTypeService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/21 1:54 PM
 */
@RestController
@Api(tags = "设备类型")
@RequestMapping("/api/deviceType")
public class DeviceTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceTypeController.class);
    @Autowired
    DeviceTypeService deviceTypeService;
//
//    @GetMapping("/api/deviceType")
//    @JpaPage
//    @ApiOperation(value = "分页查询")
//    public ResponseObject<Pagination<DeviceType>> findPage() {
//        Page<DeviceType> pageList = deviceTypeService.findPage(JpaUtils.getSpecification());
//        Pagination<DeviceType> pagination = new Pagination<DeviceType>((int) pageList.getTotalElements(), pageList.getContent());
//        return ResponseObject.success(pagination);
//    }
//
//    @ApiOperation(value = "通过ID查找")
//    @GetMapping("/api/deviceType/{id}")
//    public ResponseObject findOne(@PathVariable("id") Long id) {
//        Optional<DeviceType> entity = deviceTypeService.findById(id);
//        if (entity.isPresent()) {
//            return ResponseObject.success(deviceTypeService.toDto(entity.get()));
//        }
//        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
//    }
//
//    @PostMapping("/api/deviceType")
//    @ApiOperation(value = "保存实体")
//    public ResponseObject save(@ApiParam @Validated @RequestBody DeviceTypeDto dto) {
//        DeviceType entity = new DeviceType();
//        BeanUtils.copyProperties(dto, entity);
//        deviceTypeService.save(entity);
//        return ResponseObject.success("OK");
//    }
//
//    @PatchMapping("/api/deviceType")
//    @ApiOperation(value = "部分更新")
//    public ResponseObject patchUpdate(@ApiParam @RequestBody DeviceTypeDto dto) {
//        Optional<DeviceType> op = deviceTypeService.findById(dto.getId());
//        if (op.isPresent()) {
//            DeviceType entity = op.get();
//            BeanUtils.copyProperties(dto, entity);
//            deviceTypeService.save(entity);
//            return ResponseObject.success("OK");
//        }
//        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
//    }
//
//    @ApiOperation(value = "删除单个实体")
//    @DeleteMapping("/api/deviceType/{id}")
//    public ResponseObject delete(@PathVariable("id") Long id) {
//        deviceTypeService.deleteById(id);
//        return ResponseObject.success("OK");
//    }

//

    @PostMapping
    @ApiOperation(value = "增加设备类型") //方法描述
    public ResponseObject addDeviceType(@ApiParam @Validated @RequestBody DeviceTypeDto deviceTypeDto){
        LOGGER.info("addDeviceType|deviceTypeDto:{} ", deviceTypeDto);
        deviceTypeService.save(deviceTypeDto);
        return ResponseObject.success(null);
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除设备类型") //方法描述
    public ResponseObject deleteDeviceType(@PathVariable("id") Long id) {
        LOGGER.info("deleteDeviceType|id:{} ", id);
        //id号是否存在
        DeviceTypeDto deviceTypeDto = deviceTypeService.findById(id);
        if (deviceTypeDto==null) {
            return ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }
        deviceTypeService.deleteById(id);

        return ResponseObject.success(null);
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "通过Id查找设备类型") //方法描述
    public ResponseObject findDeviceSystem(@PathVariable("id") Long id) {
        LOGGER.info("find|id:{} ", id);
        //id号是否存在
        DeviceTypeDto deviceTypeDto = deviceTypeService.findById(id);
        if (null == deviceTypeDto) {
            return ResponseObject.fail(ErrorEnum.DEVICE_TYPE_NOT_EXIST);
        }
        return ResponseObject.success(deviceTypeDto);
    }

    @GetMapping
    @JpaPage
    @ApiOperation(value = "获取所有设备类型，分页查询") //方法描述
    public ResponseObject getAllDeviceTypes() {
        Pagination result = deviceTypeService.findPage(JpaUtils.getSpecification());
        return ResponseObject.success(result);
    }

    @GetMapping("/all")
    @JpaPage
    @ApiOperation(value = "获取所有设备类型，不分页") //方法描述
    public ResponseObject getAll() {
        List<DeviceTypeDto> result = deviceTypeService.findAll(JpaUtils.getSpecification());
        return ResponseObject.success(result);
    }

    @GetMapping("/bySystemTypeId")
    @ApiOperation(value = "获取系统类型下所有设备类型，不分页") //方法描述
    public ResponseObject getAll(Long systemTypeId) {
        List<DeviceTypeDto> result = deviceTypeService.findDeviceTypesBySystemType(systemTypeId);
        return ResponseObject.success(result);
    }



}
