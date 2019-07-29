package com.honeywell.fireiot.water.repository;

import com.honeywell.fireiot.water.entity.WaterDailyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 9:08 AM 3/20/2019
 */
public interface WaterDailyEventRepository extends JpaRepository<WaterDailyEvent, Long>, JpaSpecificationExecutor<WaterDailyEvent> {
    int STATS_TYPE_DAY = 0;
    int STATS_TYPE_MONTH = 1;

    String EVENT_LOW_EXCEPTION="低位异常";
    String EVENT_HIGH_EXCEPTION="高位异常";


    String EVENT_PRESSURE = "pressure"; //压力
    String EVENT_LIQUID_LEVEL = "liquid_level"; //液位
    String EVENT_ELECTRIC_PRESSURE_ALARM = "electric_pressure_alarm"; //电池低电压

    WaterDailyEvent findFirstByStatsDate(String statsDate);

    List<WaterDailyEvent> findByStatsTypeAndStatsDateBetween(int statsType, String start, String end);
    List<WaterDailyEvent> findByStatsTypeAndEuiAndStatsDateBetween(int statsType, String eui, String start, String end);
}
