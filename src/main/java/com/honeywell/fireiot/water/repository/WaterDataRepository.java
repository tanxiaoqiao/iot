package com.honeywell.fireiot.water.repository;

import com.honeywell.fireiot.water.entity.WaterData;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author xiaomingCao
 */
@Repository
public interface WaterDataRepository extends PagingAndSortingRepository<WaterData, Long>, JpaSpecificationExecutor<WaterData> {

    Optional<WaterData> findTopByEuiAndFieldNameOrderByTimestampDesc(String eui, String fieldName);
    List<WaterData> findAllByEui(String eui);

    List<WaterData> findTop10ByEuiAndFieldNameAndTimestampGreaterThanEqualOrderByTimestampDesc(String eui, String fieldName, Long startTime);
    List<WaterData> findByEuiAndFieldNameAndTimestampGreaterThanEqualAndTimestampLessThanEqual(String eui, String fieldName, Long startTime, Long endTime);
}
