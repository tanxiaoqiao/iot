package com.honeywell.fireiot.fire.service;

import com.honeywell.fireiot.fire.entity.MTGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date :
 */
public interface MTGatewayService {

    void save(MTGateway entity);

    void delete(MTGateway t);

    void deleteById(Long id);

    Optional<MTGateway> findById(Long id);

    Page<MTGateway> findPage();

    Page<MTGateway> findPage(Specification<MTGateway> specification);

//    DeviceTypeDto toDto(MTGateway entity);

    List<MTGateway> findAll();

}
