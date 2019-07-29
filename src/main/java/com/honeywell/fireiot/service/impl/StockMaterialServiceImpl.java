package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.MaterialDto;
import com.honeywell.fireiot.dto.StockMaterialDto;
import com.honeywell.fireiot.entity.BaseMaterial;
import com.honeywell.fireiot.entity.MaterialDetail;
import com.honeywell.fireiot.entity.StockMaterial;
import com.honeywell.fireiot.entity.Warehouse;
import com.honeywell.fireiot.repository.BaseMaterialRepository;
import com.honeywell.fireiot.repository.MaterialDetailRepository;
import com.honeywell.fireiot.repository.StockMaterialRepository;
import com.honeywell.fireiot.repository.WarehouseRepository;
import com.honeywell.fireiot.service.BaseMaterialService;
import com.honeywell.fireiot.service.StockMaterialService;
import com.honeywell.fireiot.utils.FileUtil;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipOutputStream;

@Service
@Transactional(rollbackFor = Exception.class)
public class StockMaterialServiceImpl implements StockMaterialService {

    @Autowired
    StockMaterialRepository stockMaterialRep;

    @Autowired
    BaseMaterialRepository baseMaterialRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    BaseMaterialService baseMaterialService;

    @Autowired
    MaterialDetailRepository materialDetailRepository;

    @Override
    public void save(StockMaterial entity) {
        stockMaterialRep.save(entity);
    }

    @Override
    public void delete(StockMaterial entity) {
        stockMaterialRep.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        stockMaterialRep.deleteById(id);
    }

    @Override
    public Optional<StockMaterial> findById(Long id) {
        return stockMaterialRep.findById(id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<StockMaterial> findPage() {
        Page<StockMaterial> entityPage = stockMaterialRep.findAll(JpaUtils.getPageRequest());
        return entityPage;
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<StockMaterial> findPage(Specification<StockMaterial> specification) {
        Page<StockMaterial> entityPage = stockMaterialRep.findAll(specification, JpaUtils.getPageRequest());
        return entityPage;
    }

    @Override
    public StockMaterial toEntity(StockMaterialDto dto) {
        StockMaterial entity = new StockMaterial();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public StockMaterialDto toDto(StockMaterial entity) {
        StockMaterialDto dto = new StockMaterialDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public MaterialDto toMaterialDto(StockMaterial entity) {
        MaterialDto materialDto = new MaterialDto();
        BaseMaterial baseMaterial = baseMaterialRepository.findById(entity.getBaseMaterialId()).orElse(null);

        BeanUtils.copyProperties(entity,materialDto);
        Long stockMaterialId = entity.getId();
        if(baseMaterial != null){
            BeanUtils.copyProperties(baseMaterial,materialDto);
            materialDto.setId(stockMaterialId);
        }

        Warehouse warehouse = warehouseRepository.findById(entity.getWarehouseId()).orElse(null);
        if(warehouse != null){
            materialDto.setWarehouseName(warehouse.getName());
        }

        List<MaterialDetail> materialDetailList =  materialDetailRepository.findByStockMaterialId(entity.getId());
        if(!materialDetailList.isEmpty()){
            materialDto.setMaterialDetailList(materialDetailList);
        }

        return materialDto;
    }

    @Override
    public Long saveMaterial(MaterialDto dto)  {
        //1、保存到baseMaterial
        BaseMaterial baseMaterial = new BaseMaterial();
        BeanUtils.copyProperties(dto, baseMaterial);

        Long id =  baseMaterialService.saveBaseMaterial(baseMaterial);


        //2、保存到stockMaterial
        StockMaterial stockMaterial = new StockMaterial();
        BeanUtils.copyProperties(dto, stockMaterial);
        stockMaterial.setBaseMaterialId(id);
        stockMaterial.setId(null);
        return  stockMaterialRep.save(stockMaterial).getId();
    }



    @Override
    public Pagination<MaterialDto> findMaterialPage(Specification<StockMaterial> specification) {
        Page<StockMaterial> entityPage = stockMaterialRep.findAll(specification, JpaUtils.getPageRequest());
        List<StockMaterial> stockMaterials = entityPage.getContent();

        if((null == stockMaterials) || (stockMaterials.isEmpty())){
            return null;
        }
        List<MaterialDto> materialDtoList = new ArrayList<>();
        for(StockMaterial stockMaterial:stockMaterials){

            MaterialDto materialDto = toMaterialDto(stockMaterial);
            materialDtoList.add(materialDto);
        }
        return  new Pagination((int)entityPage.getTotalElements(), materialDtoList);
    }

    @Override
    public void exportQrPicture(List<Long> ids, HttpServletResponse response) throws IOException {
        StringBuffer zipFileName = new StringBuffer();
        zipFileName.append("EXPORT");
        zipFileName.append(Calendar.getInstance().getTimeInMillis());
        zipFileName.append("zip");
        //获取流
        ZipOutputStream zipOutputStream =  FileUtil.getZipOutputStream(zipFileName.toString(),response);

        for(Long id: ids){
            StockMaterial stockMaterial = stockMaterialRep.findById(id).orElse(null);
            if(null == stockMaterial){
                continue;
            }

            MaterialDto materialDto = toMaterialDto(stockMaterial);
            if(StringUtils.isEmpty(materialDto.getQrImage())){
                continue;
            }
            String fileName = materialDto.getWarehouseName()+ "_"+materialDto.getCode()+".png";
            byte[] data = new BASE64Decoder().decodeBuffer(materialDto.getQrImage());
            FileUtil.writeZipFile(zipOutputStream,fileName,data);
        }

        FileUtil.closeZipOutputStream(zipOutputStream);
    }

    @Override
    public List<StockMaterial> findLowAmountMaterial() {
        return stockMaterialRep.findLowAmountMaterial();
    }


    @Override
    public Long saveDto(StockMaterialDto dto) {
        return ((StockMaterial)this.stockMaterialRep.save(this.toEntity(dto))).getId();
    }

    @Override
    public boolean updateDto(StockMaterialDto dto) {
        Optional<StockMaterial> op = this.stockMaterialRep.findById(dto.getId());
        return this.saveOptional(op, dto);
    }

    @Override
    public boolean update(StockMaterial stockMaterial) {
        Optional<StockMaterial> op = this.stockMaterialRep.findById(stockMaterial.getId());
        return this.saveOptional(op, stockMaterial);
    }

    private boolean saveOptional(Optional<StockMaterial> optional, Object object) {
        if (optional.isPresent()) {
            StockMaterial entity = optional.get();
            BeanUtils.copyProperties(object, entity);
            this.stockMaterialRep.save(entity);
            return true;
        } else {
            return false;
        }
    }

}
