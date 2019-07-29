package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.MaterialDto;
import com.honeywell.fireiot.entity.BaseMaterial;
import com.honeywell.fireiot.entity.StockMaterial;
import com.honeywell.fireiot.service.BaseMaterialService;
import com.honeywell.fireiot.service.MaterialDetailService;
import com.honeywell.fireiot.service.StockMaterialService;
import com.honeywell.fireiot.service.WarehouseService;
import com.honeywell.fireiot.utils.FileUtil;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 3:55 PM 3/29/2019
 */
@RestController
@Api(tags = "仓储管理——物资管理（页面操作物资）",value = "")
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    StockMaterialService stockMaterialService;

    @Autowired
    BaseMaterialService baseMaterialService;

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    MaterialDetailService materialDetailService;


    @PostMapping
    @ApiOperation(value = "新增物资",notes = "在确定库的情况下新增物资")
    public ResponseObject saveMaterial(@ApiParam @Validated @RequestBody MaterialDto dto) throws IOException {

        Long warehouseId = dto.getWarehouseId();
        if( warehouseId == null){
            return  ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }
        if(!warehouseService.findById(warehouseId).isPresent()){
            return ResponseObject.fail(ErrorEnum.WAREHOUSE_NOT_EXIST);//仓库不存在
        }

        //唯一性
        if(!(baseMaterialService.findByCode(dto.getCode()).isEmpty())){
            return ResponseObject.fail(ErrorEnum.MATERIAL_CODE_IS_EXIST);
        }

        // 保存物资
        Long id = stockMaterialService.saveMaterial(dto);



        return ResponseObject.success(id);
    }

    @PatchMapping
    @ApiOperation(value = "更新物资")
    public ResponseObject patchUpdateMaterial(@ApiParam @RequestBody MaterialDto dto) {
        Long warehouseId = dto.getWarehouseId();
        if( warehouseId == null){
            return  ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }
        if(!warehouseService.findById(warehouseId).isPresent()){
            return ResponseObject.fail(ErrorEnum.WAREHOUSE_NOT_EXIST);//仓库不存在
        }

        StockMaterial stockMaterial = stockMaterialService.findById(dto.getId()).orElse(null);
        if(stockMaterial != null){
            return  ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }

        //唯一性
        Optional<BaseMaterial> baseMaterialOptional = baseMaterialService.findById(dto.getBaseMaterialId());
        if(!baseMaterialOptional.isPresent()){
            return  ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }
        BaseMaterial baseMaterial = baseMaterialOptional.get();
        if((baseMaterialService.checkUpdateBaseMaterial(dto.getCode(), baseMaterial.getCode()))){
            return ResponseObject.fail(ErrorEnum.MATERIAL_CODE_IS_EXIST);
        }


        BeanUtils.copyProperties(dto, baseMaterial);
        BeanUtils.copyProperties(dto,stockMaterial);

        // 保存物资
        stockMaterialService.save(stockMaterial);
        baseMaterialService.save(baseMaterial);
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }


    @ApiOperation(value = "删除物资")
    @DeleteMapping("/{id}")
    public ResponseObject<String> deleteMaterial(@PathVariable("id") Long id) {

        StockMaterial stockMaterial = stockMaterialService.findById(id).orElse(null);
        if(null == stockMaterial){
            return  ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }

        //判断当前物资是否还有库存
        float amount = stockMaterial.getValidAmount();
        if(Float.compare(amount, 0) > 0)
        {
            return  ResponseObject.fail(ErrorEnum.MATERIAL_STOCK_IS_EXIST);
        }

        stockMaterialService.deleteById(id);
        // 删除其下对应的物资详情
        materialDetailService.deleteByStockMaterialId(id);

        //对于基础物资的删除，当基础物资对应所有的库存物资都无的情况下
        Long  baseMaterialId = stockMaterial.getBaseMaterialId();
        if(baseMaterialService.checkDeleteBaseMaterial(baseMaterialId)){
            baseMaterialService.deleteById(baseMaterialId);
        }

        return ResponseObject.success("OK");
    }


    @ApiOperation(value = "通过ID查找")
    @GetMapping("/{id}")
    public ResponseObject findMaterial(@PathVariable("id") Long id) throws Exception {
        Optional<StockMaterial> entity = stockMaterialService.findById(id);
        if (entity.isPresent()) {
            MaterialDto materialDto =  stockMaterialService.toMaterialDto(entity.get());
            FileOutputStream outputStream = new FileOutputStream("./materil.png");
            FileUtil.saveBase64ToFile(materialDto.getQrImage(),outputStream);
            return ResponseObject.success(materialDto);
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @GetMapping
    @JpaPage
    @ApiOperation(value = "物资分页查询")
    public ResponseObject<Pagination<MaterialDto>> findMaterialPage() {
        return ResponseObject.success(stockMaterialService.findMaterialPage(JpaUtils.getSpecification()));
    }


    @PostMapping("/exportFile")
    @ApiOperation(value = "导出二维码")
    public ResponseObject exportQrPicture(@RequestParam(value = "ids",required = true) List<Long> ids,  HttpServletResponse response) throws IOException {
        if(null == ids){
            return  ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }

        stockMaterialService.exportQrPicture(ids, response);

        return  ResponseObject.success("OK");
    }


}
