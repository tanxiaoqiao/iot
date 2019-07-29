package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.BaseMaterialDto;
import com.honeywell.fireiot.entity.BaseMaterial;
import com.honeywell.fireiot.entity.StockMaterial;
import com.honeywell.fireiot.repository.BaseMaterialRepository;
import com.honeywell.fireiot.repository.StockMaterialRepository;
import com.honeywell.fireiot.service.BaseMaterialService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.QRcodeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class BaseMaterialServiceImpl implements BaseMaterialService {

    @Autowired
    BaseMaterialRepository baseMaterialRep;

    @Autowired
    StockMaterialRepository stockMaterialRepository;

    @Override
    public void save(BaseMaterial entity) {
        baseMaterialRep.save(entity);
    }

    @Override
    public void delete(BaseMaterial entity) {
        baseMaterialRep.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        baseMaterialRep.deleteById(id);
    }

    @Override
    public Optional<BaseMaterial> findById(Long id) {
        return baseMaterialRep.findById(id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<BaseMaterial> findPage() {
        Page<BaseMaterial> entityPage = baseMaterialRep.findAll(JpaUtils.getPageRequest());
        return entityPage;
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<BaseMaterial> findPage(Specification<BaseMaterial> specification) {
        Page<BaseMaterial> entityPage = baseMaterialRep.findAll(specification, JpaUtils.getPageRequest());
        return entityPage;
    }

    @Override
    public BaseMaterial toEntity(BaseMaterialDto dto) {
        BaseMaterial entity = new BaseMaterial();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public BaseMaterialDto toDto(BaseMaterial entity) {
        BaseMaterialDto dto = new BaseMaterialDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<BaseMaterial> findByCode(String code) {
        return baseMaterialRep.findByCode(code);
    }

    @Override
    public Long saveBaseMaterial(BaseMaterial baseMaterial) {
        //二维码信息：物资名称+物资型号+物资编码
        String qrcode = baseMaterial.getName()+"|"+baseMaterial.getModel()+"|" + baseMaterial.getCode();
        baseMaterial.setQrCode(qrcode);
        try {
            BufferedImage bufferedImage = QRcodeUtil.getQRcodeWithNote(qrcode,qrcode);
            baseMaterial.setQrImage(QRcodeUtil.toBase64String(bufferedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return   baseMaterialRep.save(baseMaterial).getId();
    }

    @Override
    public boolean checkUpdateBaseMaterial(String code, String oldCode) {

        List<BaseMaterial> baseMaterialList = baseMaterialRep.findByCode(code);

        if(baseMaterialList.size() > 1){
            return true;
        }
        if( ( 1 == baseMaterialList.size()) && (!(baseMaterialList.get(0).getName().equals(oldCode)))){
            return true;
        }

        return false;

    }

    /**
     * 判断对应基础物资下是否有库存物资，或者库存物资个数为0
     * @param id
     * @return
     */
    @Override
    public boolean checkDeleteBaseMaterial(Long id) {


        List<StockMaterial> stockMaterialList = stockMaterialRepository.findByBaseMaterialId(id);
        if(stockMaterialList.isEmpty()){
            return true;
        }

        float amount = 0;
        for(StockMaterial stockMaterial:stockMaterialList){
            amount += stockMaterial.getValidAmount();
            if(Float.compare(amount, 0 )> 0){
                return  false;
            }
        }
        if(Float.compare(amount, 0) < 0){
            return true;
        }

        return false;
    }


}
