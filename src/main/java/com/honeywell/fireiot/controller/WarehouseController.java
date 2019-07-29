package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.WarehouseDto;
import com.honeywell.fireiot.entity.Warehouse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Api(tags = "仓储管理——仓库管理")
@RequestMapping("/api/warehouse")
public class WarehouseController {

    @Autowired
    WarehouseService warehouseService;


    @GetMapping
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<WarehouseDto>> findPage() {
        Page<Warehouse> pageList = warehouseService.findPage();
        int total = (int)pageList.getTotalElements();
        List<WarehouseDto> warehouseDtoList = new ArrayList<>();
        if(total > 0 ){
            List<Warehouse> warehouseList = pageList.getContent();
            for(Warehouse warehouse:warehouseList){
                WarehouseDto warehouseDto = warehouseService.toDto(warehouse);
                warehouseDtoList.add(warehouseDto);
            }
        }

        Pagination<WarehouseDto> pagination = new Pagination<WarehouseDto>(total, warehouseDtoList);
        return ResponseObject.success(pagination);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        Optional<Warehouse> entity = warehouseService.findById(id);
        if (entity.isPresent()) {
            return ResponseObject.success(entity.get());
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @PostMapping
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody WarehouseDto warehouse) {
        warehouseService.save(warehouseService.toEntity(warehouse));
        return ResponseObject.success("OK");
    }


    @PatchMapping
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody Warehouse warehouse) {
        Optional<Warehouse> op = warehouseService.findById(warehouse.getId());
        if (op.isPresent()) {
            Warehouse entity = op.get();
            BeanUtils.copyProperties(warehouse, entity);
            warehouseService.save(entity);
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/{id}")
    public ResponseObject<String> delete(@PathVariable("id") Long id) {
        warehouseService.deleteById(id);
        return ResponseObject.success("OK");
    }
}
