package com.honeywell.fireiot.water.repository;

import com.honeywell.fireiot.water.entity.WaterEvent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author xiaoming.cao
 */
@Repository
public interface WaterEventRepository extends PagingAndSortingRepository<WaterEvent, Long>, JpaSpecificationExecutor<WaterEvent> {
    Optional<WaterEvent> findTopByEuiAndFieldNameOrderByStartTimeDesc(String eui, String fieldName);


}
