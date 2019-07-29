package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.SystemTypeDto;
import com.honeywell.fireiot.entity.SystemType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

/*
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:56 AM
 */
public interface SystemTypeService {

    void save(SystemType entity);

    void delete(SystemType t);

    void deleteById(Long id);

    Optional<SystemType> findById(Long id);

    Page<SystemType> findPage();

    Page<SystemType> findPage(Specification<SystemType> specification);

    SystemTypeDto toDto(SystemType entity);

    List<SystemTypeDto> findAllDeviceSystem();
}
