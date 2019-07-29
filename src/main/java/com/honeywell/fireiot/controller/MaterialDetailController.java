package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.MaterialDetailDto;
import com.honeywell.fireiot.entity.MaterialDetail;
import com.honeywell.fireiot.service.MaterialDetailService;
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

@RestController
@Api(tags = "仓储管理——物资详情(数据库数据)",value = "属于库存物资的详情属性——入库物资")
@RequestMapping("/api/materialDetail")
public class MaterialDetailController {

    @Autowired
    MaterialDetailService materialDetailService;


    @GetMapping
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<MaterialDetail>> findPage() {
        Page<MaterialDetail> pageList = materialDetailService.findPage();
        Pagination<MaterialDetail> pagination = new Pagination<MaterialDetail>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(pagination);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        Optional<MaterialDetail> entity = materialDetailService.findById(id);
        if (entity.isPresent()) {
            return ResponseObject.success(materialDetailService.toDto(entity.get()));
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @PostMapping
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody MaterialDetailDto dto) {
        MaterialDetail entity = materialDetailService.toEntity(dto);
        materialDetailService.save(entity);
        return ResponseObject.success("OK");
    }


    @PatchMapping
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody MaterialDetailDto dto) {
        Optional<MaterialDetail> op = materialDetailService.findById(dto.getId());
        if (op.isPresent()) {
            MaterialDetail entity = op.get();
            BeanUtils.copyProperties(dto, entity);
            materialDetailService.save(entity);
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/{id}")
    public ResponseObject<String> delete(@PathVariable("id") Long id) {
        materialDetailService.deleteById(id);
        return ResponseObject.success("OK");
    }
}
