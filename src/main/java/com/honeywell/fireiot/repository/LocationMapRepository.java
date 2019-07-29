package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.LocationMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:51 PM 04/22/2019
 */
@Repository
public interface LocationMapRepository extends JpaRepository<LocationMap, Long>, JpaSpecificationExecutor<LocationMap> {

}
