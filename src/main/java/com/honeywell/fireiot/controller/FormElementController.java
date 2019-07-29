package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.FormInputType;
import com.honeywell.fireiot.dto.FormElementDto;
import com.honeywell.fireiot.entity.FormElement;
import com.honeywell.fireiot.entity.FormStructure;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.service.FormElementService;
import com.honeywell.fireiot.service.FormStructureService;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 表单元素
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/28 1:49 PM
 */
@RestController
@Api(tags = "动态表单元素管理")
@Slf4j
public class FormElementController {

    @Autowired
    FormElementService formElementService;
    @Autowired
    FormStructureService formStructureService;

    private static Map<String, String> classType = new HashMap<String, String>();

    static {
        classType.put("设备", "/api/device/{id}");
        classType.put("单位", "/api/company/{id}");
    }

//    @GetMapping("/api/formElement")
//    @JpaPage
//    @ApiOperation(value = "分页查询")
//    public ResponseObject<Pagination<FormElement>> findPage() {
//        Page<FormElement> pageList = formElementService.findPage();
//
//        Pagination<FormElement> pagination = new Pagination<FormElement>((int) pageList.getTotalElements(), pageList.getContent());
//
//        return ResponseObject.success(pagination);
//    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/api/formElement/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        Optional<FormElement> entity = formElementService.findById(id);
        if (entity.isPresent()) {
            return ResponseObject.success(formElementService.toDto(entity.get()));
        }
        throw new BusinessException(ErrorEnum.NOT_FOUND);
    }

    @ApiOperation(value = "查找元素支持的业务类型")
    @GetMapping("/api/formElement/classType")
    public ResponseObject findOne() {
        return ResponseObject.success(classType);
    }

    @PostMapping("/api/formElement")
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody FormElementDto dto) {
        // 参数校验
        if (dto.getFormId() == null || dto.getFormId() <= 0) {
            throw new BusinessException(ErrorEnum.ELEMENT_FORM_NOT_NULL);
        }
        Optional<FormStructure> formOp = formStructureService.findById(dto.getFormId());
        formOp.orElseThrow(() -> new BusinessException(ErrorEnum.ELEMENT_FORM_NOT_FOUND));

        FormElement entity = formElementService.toEntity(dto);
        entity.setUuid(UUID.randomUUID().toString());
        entity.setForm(formOp.get());
        formElementService.save(entity);
        return ResponseObject.success("OK");
    }


    @PatchMapping("/api/formElement")
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody FormElementDto dto) {

        Optional<FormElement> op = formElementService.findById(dto.getId());
        op.orElseThrow(() -> new BusinessException(ErrorEnum.UPDATE_ERROR));

        // 参数校验
        if (dto.getFormId() == null || dto.getFormId() <= 0) {
            throw new BusinessException(ErrorEnum.ELEMENT_FORM_NOT_NULL);
        }
        Optional<FormStructure> formOp = formStructureService.findById(dto.getFormId());
        formOp.orElseThrow(() -> new BusinessException(ErrorEnum.ELEMENT_FORM_NOT_FOUND));

        FormElement entity = op.get();
        BeanUtils.copyProperties(dto, entity);
        // TODO
        BeanUtils.copyProperties(dto.getValidator(), entity.getValidator());
        if (dto.getInputType() != null) {
            entity.setInputType(FormInputType.valueOf(dto.getInputType()));
        }
        entity.setForm(formOp.get());
        formElementService.save(entity);
        return ResponseObject.success("OK");
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/api/formElement/{id}")
    public ResponseObject<String> delete(@PathVariable("id") Long id) {
        formElementService.deleteById(id);
        return ResponseObject.success("OK");
    }
}
