package com.honeywell.fireiot.fire.service;

import com.honeywell.fireiot.fire.entity.MTPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date :
 */
public interface MTPointService {

    void save(MTPoint entity);

    void delete(MTPoint t);

    void deleteById(Long id);

    Optional<MTPoint> findById(Long id);

    Page<MTPoint> findPage();

    Page<MTPoint> findPage(Specification<MTPoint> specification);

//    DeviceTypeDto toDto(MTPoint entity);

    List<MTPoint> findAll();

    List<MTPoint> findByGatewayId(Long id);
}
