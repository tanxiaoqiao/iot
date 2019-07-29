package com.honeywell.fireiot.water.service;

import com.honeywell.fireiot.repository.WaterDeviceRepository;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.water.WaterEventStatsBo;
import com.honeywell.fireiot.water.entity.WaterDailyEvent;
import com.honeywell.fireiot.water.repository.WaterDailyEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class WaterDailyEventService {

    @Autowired
    WaterDailyEventRepository waterDailyEventRepository;

    @Autowired
    WaterEventService waterEventService;

    @Autowired
    WaterDeviceRepository waterDeviceRepository;


    public List<WaterDailyEvent> findAll() {
        return waterDailyEventRepository.findAll();
    }

    public void save(WaterDailyEvent entity) {
        waterDailyEventRepository.save(entity);
    }

    public void delete(WaterDailyEvent t) {
        waterDailyEventRepository.delete(t);
    }

    public void deleteById(Long id) {
        waterDailyEventRepository.deleteById(id);
    }

    public Optional<WaterDailyEvent> findById(Long id) {
        Optional<WaterDailyEvent> opt = waterDailyEventRepository.findById(id);
        return opt;
    }

    public Page<WaterDailyEvent> findPage() {
        Page<WaterDailyEvent> page = waterDailyEventRepository.findAll(JpaUtils.getPageRequest());
        return page;
    }


    public Pagination findPage(Specification<WaterDailyEvent> specification) {
        Page<WaterDailyEvent> pageList = waterDailyEventRepository.findAll(specification, JpaUtils.getPageRequest());
        Pagination page = new Pagination((int) pageList.getTotalElements(), pageList.getContent());
        return page;
    }

    public void calculateDailyEvent(LocalDate localDate) {
        String dataStats = localDate.toString();

        if (waterDailyEventRepository.findFirstByStatsDate(dataStats) != null) {
            log.info("====== 水系统日统计事件数据已存在：{}", dataStats);
            return;
        }

        WaterDailyEvent waterDailyEvent = new WaterDailyEvent();
        waterDailyEvent.setStatsDate(localDate.toString());
        waterDailyEvent.setStatsType(WaterDailyEventRepository.STATS_TYPE_DAY);


        // 获取一天的开始和结束时间
        long start = LocalDateTime.of(localDate, LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = LocalDateTime.of(localDate, LocalTime.MAX).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        log.info("startTime: {} ;endTime :{}", start, end);

        saveWaterDailyEvent(waterDailyEvent, start, end);
    }


    public void calculateMonthEvent(LocalDate localDate) {
        String dataStats = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        if (waterDailyEventRepository.findFirstByStatsDate(dataStats) != null) {
            log.info("====== 水系统月统计事件数据已存在：{}", dataStats);
            return;
        }

        WaterDailyEvent waterDailyEvent = new WaterDailyEvent();
        waterDailyEvent.setStatsDate(dataStats);
        waterDailyEvent.setStatsType(WaterDailyEventRepository.STATS_TYPE_MONTH);

        // 获取当月的的开始和结束时间
        long start = LocalDateTime.of(localDate.with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = LocalDateTime.of(localDate, LocalTime.MAX).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        log.info("startTime: {} ;endTime :{}", start, end);

        saveWaterDailyEvent(waterDailyEvent, start, end);

    }

    private void saveWaterDailyEvent(WaterDailyEvent waterDailyEvent, long start, long end) {
        List<String> euiList = waterDeviceRepository.findAllEui();
        euiList.forEach(eui -> {
            waterDailyEvent.setLowException(waterEventService.countByWaterEventStatsBo(new WaterEventStatsBo(start, end, WaterDailyEventRepository.EVENT_LOW_EXCEPTION, null, eui)));
            waterDailyEvent.setHighException(waterEventService.countByWaterEventStatsBo(new WaterEventStatsBo(start, end, WaterDailyEventRepository.EVENT_HIGH_EXCEPTION, null, eui)));
            waterDailyEvent.setCreateTime(LocalDateTime.now());
            waterDailyEventRepository.save(waterDailyEvent);
        });
    }


    public List<WaterDailyEvent> findByStatsTypeAndStatsDateBetween(int statsType, String start, String end) {
        return waterDailyEventRepository.findByStatsTypeAndStatsDateBetween(statsType, start, end);
    }

    public List<WaterDailyEvent> findByStatsTypeAndEuiAndStatsDateBetween(int statsType, String eui, String start, String end) {
        return waterDailyEventRepository.findByStatsTypeAndEuiAndStatsDateBetween(statsType, eui, start, end);
    }
}
