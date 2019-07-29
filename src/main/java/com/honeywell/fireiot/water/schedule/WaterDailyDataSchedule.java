package com.honeywell.fireiot.water.schedule;

import com.honeywell.fireiot.service.WaterDeviceService;
import com.honeywell.fireiot.water.service.WaterDailyDataService;
import com.honeywell.fireiot.water.service.WaterDailyEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 日常数据统计定时器
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/1/21 3:35 PM
 */
@Component
@Configuration
@EnableScheduling
@Slf4j
public class WaterDailyDataSchedule {

    @Autowired
    WaterDeviceService waterDeviceService;
    @Autowired
    WaterDailyDataService waterDailyDataService;
    @Autowired
    WaterDailyEventService waterDailyEventService;


    @Scheduled(cron = "0 0 0 0/1 * ?")
    public void dailyDataStatistics() {
        LocalDate calculateDate = LocalDate.now().minusDays(1);
        log.info("====== 水系统日数据统计开始：{}", calculateDate);
        waterDailyDataService.calculateDailyDataAndSave(calculateDate);
        log.info("====== 水系统日数据统计完成，生成 {} 数据。", calculateDate);
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void currentDailyDataStatistics() {
        LocalDate calculateDate = LocalDate.now();
        log.info("====== 水系统当天数据统计：{}", calculateDate);
        waterDailyDataService.calculateDailyDataAndSave(calculateDate);
        log.info("====== 水系统当天数据统计完成");
    }

//    ---------------------增加------------------------------
    /**
     * 每天00：02开始统计每日的水系统事件
     */
    @Scheduled(cron = "0 2 0 * * ?")
    public void dailyEventStatistics() {
        LocalDate calculateDate = LocalDate.now().minusDays(1);
        log.info("====== 水系统日统计事件数据开始：{}", calculateDate);
        waterDailyEventService.calculateDailyEvent(calculateDate);
        log.info("====== 水系统日统计事件数据完成，生成 {} 数据。", calculateDate);
    }

    /**
     * 每月1号1点1分开始统计上个月的水系统事件
     */
    @Scheduled(cron = "0 1 1 1 * ?")
    public void monthEventStatistics() {
        LocalDate calculateDate = LocalDate.now().minusDays(1);
        log.info("====== 水系统月统计事件数据开始：{}", calculateDate);
        waterDailyEventService.calculateMonthEvent(calculateDate);
        log.info("====== 水系统月统计事件数据完成，生成 {} 数据。", calculateDate);
    }
}
