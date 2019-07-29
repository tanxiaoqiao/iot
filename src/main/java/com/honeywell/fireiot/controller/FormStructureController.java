package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.FormStructureDto;
import com.honeywell.fireiot.entity.FormStructure;
import com.honeywell.fireiot.service.FormStructureService;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * 表单结构控制类
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/28 1:49 PM
 */
@RestController
@Api(tags = "动态表单结构管理")
public class FormStructureController {

    @Autowired
    FormStructureService formStructureService;

    @GetMapping("/api/formStructure")
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<FormStructure>> findPage() {
        Page<FormStructure> pageList = formStructureService.findPage();
        Pagination<FormStructure> pagination = new Pagination<FormStructure>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(pagination);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/api/formStructure/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        Optional<FormStructure> entity = formStructureService.findById(id);
        if (entity.isPresent()) {
            return ResponseObject.success(formStructureService.toDto(entity.get()));
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @ApiOperation(value = "通过uuid查找")
    @GetMapping("/api/formStructure/uuid/{uuid}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "唯一编号", dataType = "string", required = true)
    })
    public ResponseObject findByUUID(@PathVariable("uuid") String uuid) {
        FormStructure entity = formStructureService.findByUUID(uuid);
        if (entity != null) {
            return ResponseObject.success(formStructureService.toDto(entity));
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @PostMapping("/api/formStructure")
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody FormStructureDto dto) {
        FormStructure entity = formStructureService.toEntity(dto);

        entity.setUuid(UUID.randomUUID().toString());
        formStructureService.save(entity);
        return ResponseObject.success("OK");
    }
    @PatchMapping("/api/formStructure")
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody FormStructureDto dto) {
        Optional<FormStructure> op = formStructureService.findById(dto.getId());
        if (op.isPresent()) {
            FormStructure entity = op.get();
            BeanUtils.copyProperties(dto, entity);
            formStructureService.save(entity);
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }
    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/api/formStructure/{id}")
    public ResponseObject<String> delete(@PathVariable("id") Long id) {
        formStructureService.deleteById(id);
        return ResponseObject.success("OK");
    }
}
