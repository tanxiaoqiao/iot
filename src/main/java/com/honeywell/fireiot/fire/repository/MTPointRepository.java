package com.honeywell.fireiot.fire.repository;

import com.honeywell.fireiot.fire.entity.MTPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:51 PM 12/17/2018
 */
@Repository
public interface MTPointRepository extends JpaRepository<MTPoint, Long>, JpaSpecificationExecutor<MTPoint> {
    
    List<MTPoint> findByFireDevice_mtGateway_Id(Long id);

    List<MTPoint> findByFireDevice_Id(Long id);
}