package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.SystemTypeDto;
import com.honeywell.fireiot.entity.SystemType;
import com.honeywell.fireiot.repository.SystemTypeRepository;
import com.honeywell.fireiot.service.SystemTypeService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:57 AM
 */
@Service
public class SystemTypeServiceImpl implements SystemTypeService {

    @Autowired
    SystemTypeRepository systemTypeRepo;

    @Override
    public void save(SystemType entity) {
        systemTypeRepo.save(entity);
    }

    @Override
    public void delete(SystemType t) {
        systemTypeRepo.delete(t);
    }

    @Override
    public void deleteById(Long id) {
        systemTypeRepo.deleteById(id);
    }

    @Override
    public Optional<SystemType> findById(Long id) {
        Optional<SystemType> opt = systemTypeRepo.findById(id);
        return opt;
    }

    @Override
    public Page<SystemType> findPage() {
        Page<SystemType> page = systemTypeRepo.findAll(JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public Page<SystemType> findPage(Specification<SystemType> specification) {
        Page<SystemType> page = systemTypeRepo.findAll(specification, JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public SystemTypeDto toDto(SystemType entity) {
        SystemTypeDto dto = new SystemTypeDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<SystemTypeDto> findAllDeviceSystem() {
        List<SystemTypeDto> result = new ArrayList<>();
        List<SystemType> allSystemTypes = systemTypeRepo.findByParentSystemType(null);
        for (SystemType systemType : allSystemTypes) {
            SystemTypeDto dto = new SystemTypeDto();
            domain2Dto(systemType, dto);

            result.add(dto);
        }
        return result;
    }
    private void domain2Dto(SystemType systemType, SystemTypeDto systemTypeDto) {
        BeanUtils.copyProperties(systemType, systemTypeDto);
        Long parentId = null;
        SystemType parentSystemType = systemType.getParentSystemType();
        if(parentSystemType != null){
            parentId = parentSystemType.getId();
        }
        systemTypeDto.setParentId(parentId);
        if (systemType.getChildList() != null) {
            List<SystemTypeDto> childSystems = new ArrayList<>();
            for (SystemType childSystem : systemType.getChildList()) {
                SystemTypeDto childDto = new SystemTypeDto();
                domain2Dto(childSystem,  childDto);
                childDto.setParentId(systemType.getId());
                childSystems.add(childDto);
            }
            systemTypeDto.setChildren(childSystems);
        }
    }
}
