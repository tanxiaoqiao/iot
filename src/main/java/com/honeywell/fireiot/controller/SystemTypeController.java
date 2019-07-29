package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.SystemTypeDto;
import com.honeywell.fireiot.entity.SystemType;
import com.honeywell.fireiot.service.SystemTypeService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "系统类型")
public class SystemTypeController {
    @Autowired
    SystemTypeService systemTypeService;

    @GetMapping("/api/systemType")
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<SystemType>> findPage() {
        Page<SystemType> pageList = systemTypeService.findPage(JpaUtils.getSpecification());
        Pagination<SystemType> pagination = new Pagination<SystemType>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(pagination);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/api/systemType/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        Optional<SystemType> entity = systemTypeService.findById(id);
        if (entity.isPresent()) {
            return ResponseObject.success(systemTypeService.toDto(entity.get()));
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @PostMapping("/api/systemType")
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody SystemTypeDto dto) {
        SystemType entity = new SystemType();
        BeanUtils.copyProperties(dto, entity);
        systemTypeService.save(entity);
        return ResponseObject.success("OK");
    }

    @PatchMapping("/api/systemType")
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody SystemTypeDto dto) {
        Optional<SystemType> op = systemTypeService.findById(dto.getId());
        if (op.isPresent()) {
            SystemType entity = op.get();
            BeanUtils.copyProperties(dto, entity);
            systemTypeService.save(entity);
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/api/systemType/{id}")
    public ResponseObject delete(@PathVariable("id") Long id) {
        systemTypeService.deleteById(id);
        return ResponseObject.success("OK");
    }


    @GetMapping("/api/systemType/all")
    @ApiOperation(value = "获取所有设备系统") //方法描述
    public ResponseObject getAllDeviceSystems() {
        List<SystemTypeDto> deviceSystemList = systemTypeService.findAllDeviceSystem();
        return ResponseObject.success(deviceSystemList);
    }
}
