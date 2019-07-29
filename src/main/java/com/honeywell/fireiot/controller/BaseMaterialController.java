package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.BaseMaterialDto;
import com.honeywell.fireiot.entity.BaseMaterial;
import com.honeywell.fireiot.service.BaseMaterialService;
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

import java.util.Optional;

/**
 * 基础物资管理——用于测试
 */
@RestController
@Api(tags = "仓储管理——基础物资",value = "基础物资不附属于任何仓库")
@RequestMapping("/api/baseMaterial")
public class BaseMaterialController {

    @Autowired
    BaseMaterialService baseMaterialService;


    @GetMapping
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<BaseMaterial>> findPage() {
        Page<BaseMaterial> pageList = baseMaterialService.findPage();
        Pagination<BaseMaterial> pagination = new Pagination<BaseMaterial>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(pagination);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        Optional<BaseMaterial> entity = baseMaterialService.findById(id);
        if (entity.isPresent()) {
            return ResponseObject.success(baseMaterialService.toDto(entity.get()));
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @PostMapping
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody BaseMaterialDto dto) {
        BaseMaterial entity = baseMaterialService.toEntity(dto);

        baseMaterialService.save(entity);
        return ResponseObject.success("OK");
    }


    @PatchMapping
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody BaseMaterialDto dto) {
        Optional<BaseMaterial> op = baseMaterialService.findById(dto.getId());
        if (op.isPresent()) {
            BaseMaterial entity = op.get();
            BeanUtils.copyProperties(dto, entity);
            baseMaterialService.save(entity);
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/{id}")
    public ResponseObject<String> delete(@PathVariable("id") Long id) {
        baseMaterialService.deleteById(id);
        return ResponseObject.success("OK");
    }
}
