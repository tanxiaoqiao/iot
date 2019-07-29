package com.honeywell.fireiot.fire.service;

import com.honeywell.fireiot.fire.bo.FireEventStatsBo;
import com.honeywell.fireiot.fire.dto.FireEventDto;
import com.honeywell.fireiot.fire.entity.FireEvent;
import com.honeywell.fireiot.fire.entity.FireEventCount;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date :
 */
public interface FireEventService {

    void save(FireEvent entity);

    void delete(FireEvent t);

    void deleteById(Long id);

    Optional<FireEvent> findById(Long id);

    Page<FireEvent> findPage();

    Pagination findPage(Specification<FireEvent> specification);

    List<FireEvent> find(Specification<FireEvent> specification);

    FireEventDto toDto(FireEvent entity);

    List<FireEvent> findAll();

    long countByFireEventStatsBo(FireEventStatsBo fireEventStatsBo);

    List<FireEventCount> findCountByDeviceNo(String deviceNo,String startTime,String endTime);

    List<FireEventCount> findCountByDeviceNo(String deviceNo,String year);

    void updateEventCount(String deviceNo, long startTime, long endTime);
}
