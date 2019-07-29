package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.StockOrderDto;
import com.honeywell.fireiot.dto.StockOrderMaterialDto;
import com.honeywell.fireiot.entity.MaterialAndOrder;
import com.honeywell.fireiot.entity.StockOrder;
import com.honeywell.fireiot.service.MaterialAndOrderService;
import com.honeywell.fireiot.service.StockOrderService;
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
@Api(tags = "仓储管理——库存单")
@RequestMapping("/api/stockOrder")
public class StockOrderController {

    @Autowired
    StockOrderService stockOrderService;

    @Autowired
    MaterialAndOrderService materialAndOrderService;


    @GetMapping
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<StockOrder>> findPage() {
        Page<StockOrder> pageList = stockOrderService.findPage();
        Pagination<StockOrder> pagination = new Pagination<StockOrder>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(pagination);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        Optional<StockOrder> entity = stockOrderService.findById(id);
        if (entity.isPresent()) {
            return ResponseObject.success(stockOrderService.toDto(entity.get()));
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @PostMapping
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody StockOrderDto dto) {
        StockOrder entity = stockOrderService.toEntity(dto);

        stockOrderService.save(entity);
        return ResponseObject.success("OK");
    }


    @PatchMapping
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody StockOrderDto dto) {
        Optional<StockOrder> op = stockOrderService.findById(dto.getId());
        if (op.isPresent()) {
            StockOrder entity = op.get();
            BeanUtils.copyProperties(dto, entity);
            stockOrderService.save(entity);
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/{id}")
    public ResponseObject<String> delete(@PathVariable("id") Long id) {
        stockOrderService.deleteById(id);
        return ResponseObject.success("OK");
    }

    /****************************************************************************************************************************/
    /*******************************************************具体业务操作********************************************************/
    /**************************************************************************************************************************/


    //入库，出库，预定等操作
    @PostMapping({"/stock"})
    @ApiOperation("库存操作")
    public ResponseObject stockOrder(@RequestBody StockOrderMaterialDto stockOrderMaterialDto) {

        String orderNumber = stockOrderService.stockOperate(stockOrderMaterialDto);
        if(null == orderNumber){
            return  ResponseObject.fail(ErrorEnum.STOCK_FAIL);
        }
        return ResponseObject.success(orderNumber);
    }

    @JpaPage
    @ApiOperation(value = "分页查询")
    @GetMapping("/materialAndStock")
    public ResponseObject<Pagination<MaterialAndOrder>> findMaterialAndStock() {
        Page<MaterialAndOrder> pageList = materialAndOrderService.findPage();
        Pagination<MaterialAndOrder> pagination = new Pagination<MaterialAndOrder>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(pagination);
    }




}
