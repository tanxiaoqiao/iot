package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.DeviceTypeDto;
import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

/*
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:56 AM
 */
public interface DeviceTypeService {

//    void save(DeviceType entity);
//
//    void delete(DeviceType t);
//
//    void deleteById(Long id);
//
//    Optional<DeviceType> findById(Long id);
//
//    Page<DeviceType> findPage();
//
//    Page<DeviceType> findPage(Specification<DeviceType> specification);
//
//    DeviceTypeDto toDto(DeviceType entity);


    void save(DeviceTypeDto dto);


    void deleteById(Long id);

    DeviceTypeDto findById(Long id);

    Pagination<DeviceTypeDto> findPage();

    Pagination<DeviceTypeDto> findPage(Specification<DeviceType> specification);

    List<DeviceTypeDto> findAll(Specification<DeviceType> specification);

    DeviceTypeDto toDto(DeviceType entity);

    List<DeviceTypeDto> findDeviceTypesBySystemType(Long systemTypeId);

    Optional<DeviceType> findByNameAndSystemTypeId(String name, Long id);
}
