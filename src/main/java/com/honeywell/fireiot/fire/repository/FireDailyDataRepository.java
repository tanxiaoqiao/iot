package com.honeywell.fireiot.fire.repository;

import com.honeywell.fireiot.fire.entity.FireDailyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 9:08 AM 3/20/2019
 */
public interface FireDailyDataRepository extends JpaRepository<FireDailyData, Long>, JpaSpecificationExecutor<FireDailyData> {
    int STATS_TYPE_DAY = 0;
    int STATS_TYPE_MONTH = 1;

    FireDailyData findFirstByStatsDate(String statsDate);

    List<FireDailyData> findByStatsTypeAndStatsDateBetween(int statsType,String start,String end);
}
