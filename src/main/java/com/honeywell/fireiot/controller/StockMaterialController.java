package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.StockMaterialDto;
import com.honeywell.fireiot.entity.StockMaterial;
import com.honeywell.fireiot.service.BaseMaterialService;
import com.honeywell.fireiot.service.StockMaterialService;
import com.honeywell.fireiot.service.WarehouseService;
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
@Api(tags = "仓储管理——库存物资（数据库数据）",value = "依属于某个仓库")
@RequestMapping("/api")
public class StockMaterialController {

    @Autowired
    StockMaterialService stockMaterialService;

    @Autowired
    BaseMaterialService baseMaterialService;

    @Autowired
    WarehouseService warehouseService;

    @GetMapping("/stockMaterial")
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<StockMaterial>> findPage() {
        Page<StockMaterial> pageList = stockMaterialService.findPage();
        Pagination<StockMaterial> pagination = new Pagination<StockMaterial>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(pagination);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/stockMaterial/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        Optional<StockMaterial> entity = stockMaterialService.findById(id);
        if (entity.isPresent()) {
            return ResponseObject.success(stockMaterialService.toDto(entity.get()));
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @PostMapping("/stockMaterial")
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody StockMaterialDto dto) {
        StockMaterial entity = stockMaterialService.toEntity(dto);

        stockMaterialService.save(entity);
        return ResponseObject.success("OK");
    }


    @PatchMapping("/stockMaterial")
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody StockMaterialDto dto) {
        Optional<StockMaterial> op = stockMaterialService.findById(dto.getId());
        if (op.isPresent()) {
            StockMaterial entity = op.get();
            BeanUtils.copyProperties(dto, entity);
            stockMaterialService.save(entity);
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }



}
