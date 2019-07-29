package com.honeywell.fireiot.water.repository;

import com.honeywell.fireiot.water.entity.WaterDailyData;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface WaterDailyDataRepository extends PagingAndSortingRepository<WaterDailyData, Long>, JpaSpecificationExecutor<WaterDailyData> {

    @Modifying
    @Query(value = "delete from WaterDailyData entity where entity.timestamp like ?1")
    public void deleteByTimestamp(String date);

    List<WaterDailyData> findByEuiAndFieldNameAndTimestamp(String eui, String fieldName, String date);

    @Query(value = "select entity from WaterDailyData entity where entity.timestamp in ?1 and entity.eui in ?2")
    List<WaterDailyData> findByTimestampAndEui(List<String> rangeDates, List<String> euis);

}
