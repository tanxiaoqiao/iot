package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.DeviceTypeDto;
import com.honeywell.fireiot.dto.EventTypeDto;
import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.entity.EventType;
import com.honeywell.fireiot.entity.SystemType;
import com.honeywell.fireiot.repository.DeviceTypeRepository;
import com.honeywell.fireiot.repository.SystemTypeRepository;
import com.honeywell.fireiot.service.DeviceTypeService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
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
public class DeviceTypeServiceImpl implements DeviceTypeService {

    @Autowired
    DeviceTypeRepository deviceTypeRepo;

//    @Override
//    public void save(DeviceType entity) {
//        deviceTypeRepo.save(entity);
//    }
//
//    @Override
//    public void delete(DeviceType t) {
//        deviceTypeRepo.delete(t);
//    }
//
//    @Override
//    public void deleteById(Long id) {
//        deviceTypeRepo.deleteById(id);
//    }
//
//    @Override
//    public Optional<DeviceType> findById(Long id) {
//        Optional<DeviceType> opt = deviceTypeRepo.findById(id);
//        return opt;
//    }
//
//    @Override
//    public Page<DeviceType> findPage() {
//        Page<DeviceType> page = deviceTypeRepo.findAll(JpaUtils.getPageRequest());
//        return page;
//    }
//
//    @Override
//    public Page<DeviceType> findPage(Specification<DeviceType> specification) {
//        Page<DeviceType> page = deviceTypeRepo.findAll(specification, JpaUtils.getPageRequest());
//        return page;
//    }
//
//    @Override
//    public DeviceTypeDto toDto(DeviceType entity) {
//        DeviceTypeDto dto = new DeviceTypeDto();
//        BeanUtils.copyProperties(entity, dto);
//        return dto;
//    }
    @Autowired
    SystemTypeRepository systemTypeRepository;
    @Override
    public void save(DeviceTypeDto deviceTypeDto) {
        DeviceType deviceType = new DeviceType();
        BeanUtils.copyProperties(deviceTypeDto, deviceType);
        Optional<SystemType> opt = systemTypeRepository.findById(deviceTypeDto.getSystemTypeId());
        if(opt.isPresent()){
            deviceType.setSystemType(opt.get());
        }
        deviceTypeRepo.save(deviceType);
    }


    @Override
    public void deleteById(Long id) {
        deviceTypeRepo.deleteById(id);
    }

    @Override
    public DeviceTypeDto findById(Long id) {
        Optional<DeviceType> opt = deviceTypeRepo.findById(id);
        DeviceTypeDto deviceTypeDto = null;
        if(opt.isPresent()){
            deviceTypeDto =   toDto(opt.get());
        }
        return deviceTypeDto;
    }

    @Override
    public Pagination<DeviceTypeDto> findPage() {
        Page<DeviceType> page = deviceTypeRepo.findAll(JpaUtils.getPageRequest());
        Pagination<DeviceTypeDto> result = getDeviceTypeDtoPagination(page);
        return result;
    }

    @Override
    public Pagination<DeviceTypeDto> findPage(Specification<DeviceType> specification) {
        Page<DeviceType> page = deviceTypeRepo.findAll(specification, JpaUtils.getPageRequest());
        Pagination<DeviceTypeDto> result = getDeviceTypeDtoPagination(page);
        return result;
    }

    @Override
    public DeviceTypeDto toDto(DeviceType entity) {
        DeviceTypeDto dto = new DeviceTypeDto();
        BeanUtils.copyProperties(entity, dto);
        if(entity.getEventTypes()!=null){
            List<EventTypeDto> eventTypeDtos = new ArrayList<>();
            for(EventType eventType :entity.getEventTypes()){
                EventTypeDto eventTypeDto = new EventTypeDto();
                BeanUtils.copyProperties(eventType, eventTypeDto);
                eventTypeDtos.add(eventTypeDto);
            }
            dto.setEventTypes(eventTypeDtos);
        }
        return dto;
    }

    @Override
    public List<DeviceTypeDto> findDeviceTypesBySystemType(Long systemTypeId) {
        Optional<SystemType> opt = systemTypeRepository.findById(systemTypeId);
        if (opt.isPresent()) {
            List<DeviceType>  deviceTypes = deviceTypeRepo.findAllBySystemType(opt.get());
            return getDeviceTypeDtos(deviceTypes);
        }
        return null;
    }

    private List<DeviceTypeDto> getDeviceTypeDtos(List<DeviceType> deviceTypes) {
        List<DeviceTypeDto> result = new ArrayList<>();
        for (DeviceType deviceType : deviceTypes) {
            DeviceTypeDto deviceTypeDto = toDto(deviceType);
            result.add(deviceTypeDto);
        }
        return result;
    }

    private Pagination<DeviceTypeDto> getDeviceTypeDtoPagination(Page<DeviceType> page) {
        List<DeviceType> entityList =  page.getContent();
        List<DeviceTypeDto> dtoList = new ArrayList<>();
        for(DeviceType deviceType :entityList){
            DeviceTypeDto deviceTypeDto = toDto(deviceType);
            dtoList.add(deviceTypeDto);
        }
        return new Pagination<>((int) page.getTotalElements(), dtoList);
    }

    @Override
    public List<DeviceTypeDto> findAll(Specification<DeviceType> specification) {
        List<DeviceType> allRecord = deviceTypeRepo.findAll(specification);
        return getDeviceTypeDtos(allRecord);
    }
    @Override
    public Optional<DeviceType> findByNameAndSystemTypeId(String ename, Long id) {
        return deviceTypeRepo.findByDescriptionAndSystemTypeId(ename, id);
    }
}
