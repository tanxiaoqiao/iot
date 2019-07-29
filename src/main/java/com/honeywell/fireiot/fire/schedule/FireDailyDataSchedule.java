package com.honeywell.fireiot.fire.schedule;

import com.honeywell.fireiot.fire.service.FireDailyDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 5:06 PM 3/19/2019
 */
@Component
@Configuration
@EnableScheduling
@Slf4j
public class FireDailyDataSchedule {

    @Autowired
    FireDailyDataService fireDailyDataService;

    /**
     * 每天00：01开始统计每日的火警事件
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void dailyDataStatistics() {
        LocalDate calculateDate = LocalDate.now().minusDays(1);
        log.info("====== 火系统日数据统计开始：{}", calculateDate);
        fireDailyDataService.calculateDailyData(calculateDate);
        log.info("====== 火系统日数据统计完成，生成 {} 数据。", calculateDate);
    }

    /**
     * 每月1号1点开始统计上个月的火警事件
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void monthDataStatistics() {
        LocalDate calculateDate = LocalDate.now().minusDays(1);
        log.info("====== 火系统月数据统计开始：{}", calculateDate);
        fireDailyDataService.calculateMonthData(calculateDate);
        log.info("====== 火系统月数据统计完成，生成 {} 数据。", calculateDate);
    }

}
