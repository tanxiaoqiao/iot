package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.WarehouseDto;
import com.honeywell.fireiot.entity.Warehouse;
import com.honeywell.fireiot.repository.WarehouseRepository;
import com.honeywell.fireiot.service.WarehouseService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    WarehouseRepository warehouseRep;

    @Override
    public void save(Warehouse entity) {
        warehouseRep.save(entity);
    }

    @Override
    public void delete(Warehouse entity) {
        warehouseRep.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        warehouseRep.deleteById(id);
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        return warehouseRep.findById(id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<Warehouse> findPage() {
        Page<Warehouse> entityPage = warehouseRep.findAll(JpaUtils.getPageRequest());
        return entityPage;
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<Warehouse> findPage(Specification<Warehouse> specification) {
        Page<Warehouse> entityPage = warehouseRep.findAll(specification, JpaUtils.getPageRequest());
        return entityPage;
    }

    @Override
    public Warehouse toEntity(WarehouseDto dto) {
        Warehouse entity = new Warehouse();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public WarehouseDto toDto(Warehouse entity) {
        WarehouseDto dto = new WarehouseDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
