package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.MaterialDetailDto;
import com.honeywell.fireiot.entity.MaterialDetail;
import com.honeywell.fireiot.repository.MaterialDetailRepository;
import com.honeywell.fireiot.service.MaterialDetailService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class MaterialDetailServiceImpl implements MaterialDetailService {

    @Autowired
    MaterialDetailRepository materialDetailRep;

    @Override
    public void save(MaterialDetail entity) {
        materialDetailRep.saveAndFlush(entity);
    }

    @Override
    public void delete(MaterialDetail entity) {
        materialDetailRep.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        materialDetailRep.deleteById(id);
    }

    @Override
    public Optional<MaterialDetail> findById(Long id) {
        return materialDetailRep.findById(id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<MaterialDetail> findPage() {
        Page<MaterialDetail> entityPage = materialDetailRep.findAll(JpaUtils.getPageRequest());
        return entityPage;
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<MaterialDetail> findPage(Specification<MaterialDetail> specification) {
        Page<MaterialDetail> entityPage = materialDetailRep.findAll(specification, JpaUtils.getPageRequest());
        return entityPage;
    }

    @Override
    public MaterialDetail toEntity(MaterialDetailDto dto) {
        MaterialDetail entity = new MaterialDetail();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public MaterialDetailDto toDto(MaterialDetail entity) {
        MaterialDetailDto dto = new MaterialDetailDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public Long saveDto(MaterialDetailDto detailDto) {
        MaterialDetail materialDetail = this.toEntity(detailDto);
        materialDetail.setStockTime(new Date());
        materialDetail.setId(null);
        return materialDetailRep.saveAndFlush(materialDetail).getId();
    }

    @Override
    public boolean updateDto(MaterialDetailDto detailDto) {
        Optional<MaterialDetail> op = materialDetailRep.findById(detailDto.getId());
        return saveOptional(op, detailDto);
    }

    @Override
    public void deleteByStockMaterialId(Long stockMaterialId) {
        List<MaterialDetail> materialDetailList = materialDetailRep.findByStockMaterialId(stockMaterialId);
        if (!materialDetailList.isEmpty()) {
            for(MaterialDetail materialDetail: materialDetailList){
            materialDetailRep.deleteById(materialDetail.getId());
            }
        }
    }

    private boolean saveOptional(Optional<MaterialDetail> optional, Object object) {
        if (optional.isPresent()) {
            MaterialDetail entity = (MaterialDetail)optional.get();
            BeanUtils.copyProperties(object, entity);
            materialDetailRep.save(entity);
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean update(MaterialDetail materialDetail) {
        Optional<MaterialDetail> op = materialDetailRep.findById(materialDetail.getId());
        return saveOptional(op, materialDetail);
    }
}
