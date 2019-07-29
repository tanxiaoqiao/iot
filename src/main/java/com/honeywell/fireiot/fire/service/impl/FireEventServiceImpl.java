package com.honeywell.fireiot.fire.service.impl;

import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.EventType;
import com.honeywell.fireiot.fire.bo.FireEventStatsBo;
import com.honeywell.fireiot.fire.dto.FireEventDto;
import com.honeywell.fireiot.fire.entity.FireEvent;
import com.honeywell.fireiot.fire.entity.FireEventCount;
import com.honeywell.fireiot.fire.repository.FireEventRepository;
import com.honeywell.fireiot.fire.service.FireEventService;
import com.honeywell.fireiot.repository.BusinessDeviceRepository;
import com.honeywell.fireiot.repository.FireEventCountRepository;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class FireEventServiceImpl implements FireEventService {

    @Autowired
    FireEventRepository fireEventRepo;
    @Autowired
    FireEventCountRepository fireEventCountRepository;

    @Autowired
    BusinessDeviceRepository businessDeviceRepo;
    @Override
    public void save(FireEvent entity) {
        fireEventRepo.save(entity);
    }

    @Override
    public void delete(FireEvent t) {
        fireEventRepo.delete(t);
    }

    @Override
    public void deleteById(Long id) {
        fireEventRepo.deleteById(id);
    }

    @Override
    public Optional<FireEvent> findById(Long id) {
        Optional<FireEvent> opt = fireEventRepo.findById(id);
        return opt;
    }

    @Override
    public Page<FireEvent> findPage() {
        Page<FireEvent> page = fireEventRepo.findAll(JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public Pagination findPage(Specification<FireEvent> specification) {
        Page<FireEvent> pageList = fireEventRepo.findAll(specification, JpaUtils.getPageRequest());
        Pagination page = new Pagination((int)pageList.getTotalElements(),pageList.getContent());
        return page;
    }

    @Override
    public   List<FireEvent> find(Specification<FireEvent> specification){
        List<FireEvent> results = fireEventRepo.findAll(specification);

       return results;
    }

    @Override
    public List<FireEvent> findAll() {
        return fireEventRepo.findAll();
    }


    @Override
    public FireEventDto toDto(FireEvent entity) {
        FireEventDto dto = new FireEventDto();
        BeanUtils.copyProperties(entity, dto);
        dto.setEventId(entity.getId());
        return dto;
    }

    @Override
    public long countByFireEventStatsBo(FireEventStatsBo fireEventStatsBo) {
        Specification<FireEvent> spec = (root, cb, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StringUtils.hasText(fireEventStatsBo.getEventStatus())){
                predicates.add(builder.equal(root.get("eventStatus"), fireEventStatsBo.getEventStatus()));
            }
            if(StringUtils.hasText(fireEventStatsBo.getEventType())){
                predicates.add(builder.equal(root.get("eventType"), fireEventStatsBo.getEventType()));
            }
            if(Objects.nonNull(fireEventStatsBo.getStartDateTime())){
                predicates.add(builder.greaterThanOrEqualTo(root.get("createDatetime"), fireEventStatsBo.getStartDateTime()));
            }
            if(Objects.nonNull(fireEventStatsBo.getEndDateTime())){
                predicates.add(builder.lessThanOrEqualTo(root.get("createDatetime"), fireEventStatsBo.getEndDateTime()));
            }
            if(CollectionUtils.isEmpty(predicates)){
                return null;
            }
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return fireEventRepo.count(spec);
    }

    public List<FireEventCount> findCountByDeviceNo(String deviceNo, String startTime, String endTime){
       List<FireEventCount>  results = fireEventCountRepository.findByDeviceNoAndCountDateIsBetween(deviceNo,startTime,endTime);
        return results;
    }

    public List<FireEventCount> findCountByDeviceNo(String deviceNo,String year){
        List<FireEventCount>  results =fireEventCountRepository.findByDeviceNoaAndCountDate(deviceNo,year);
        return results;
    }


    public void updateEventCount(String deviceNo, long startTime, long endTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = new Date(startTime);
        String countDate=  format.format(date);
        List<BusinessDevice> devices = businessDeviceRepo.findByDeviceNo(deviceNo);
        if (!devices.isEmpty()) {
            BusinessDevice device=devices.get(0);
            List<EventType> eventTypes = device.getDeviceType().getEventTypes();
           for (EventType eventType :eventTypes){
                Long count =  fireEventRepo.countByDeviceIdAndEventTypeAndEventStatusAndCreateDatetimeIsBetween(deviceNo,eventType.getName(),"Add",startTime,endTime);
               FireEventCount fireEventCount = fireEventCountRepository.findByDeviceNoAndCountDateAndEventType(deviceNo,countDate,eventType.getName());
               if(fireEventCount!=null){
                   fireEventCount.setCountNumber(count);
               }else{
                   fireEventCount = new FireEventCount(deviceNo,eventType.getName(),countDate,count);
               }
               fireEventCountRepository.save(fireEventCount);
            }
        }
    }


}
