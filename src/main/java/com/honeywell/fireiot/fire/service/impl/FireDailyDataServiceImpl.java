package com.honeywell.fireiot.fire.service.impl;

import com.honeywell.fireiot.fire.bo.FireEventStatsBo;
import com.honeywell.fireiot.fire.dto.FireDailyDataDto;
import com.honeywell.fireiot.fire.entity.FireDailyData;
import com.honeywell.fireiot.fire.repository.FireDailyDataRepository;
import com.honeywell.fireiot.fire.repository.FireEventRepository;
import com.honeywell.fireiot.fire.service.FireDailyDataService;
import com.honeywell.fireiot.fire.service.FireEventService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
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
public class FireDailyDataServiceImpl implements FireDailyDataService {

    @Autowired
    FireDailyDataRepository fireDailyDataRepo;

    @Autowired
    FireEventService fireEventService;

    @Override
    public List<FireDailyData> findAll() {
        return fireDailyDataRepo.findAll();
    }

    @Override
    public void save(FireDailyData entity) {
        fireDailyDataRepo.save(entity);
    }

    @Override
    public void delete(FireDailyData t) {
        fireDailyDataRepo.delete(t);
    }

    @Override
    public void deleteById(Long id) {
        fireDailyDataRepo.deleteById(id);
    }

    @Override
    public Optional<FireDailyData> findById(Long id) {
        Optional<FireDailyData> opt = fireDailyDataRepo.findById(id);
        return opt;
    }

    @Override
    public Page<FireDailyData> findPage() {
        Page<FireDailyData> page = fireDailyDataRepo.findAll(JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public Pagination findPage(Specification<FireDailyData> specification) {
        Page<FireDailyData> pageList = fireDailyDataRepo.findAll(specification, JpaUtils.getPageRequest());
        Pagination page = new Pagination((int)pageList.getTotalElements(),pageList.getContent());
        return page;
    }

    @Override
    public FireDailyDataDto toDto(FireDailyData entity) {
        FireDailyDataDto fireDailyDataDto = new FireDailyDataDto();
        return fireDailyDataDto;
    }


    @Override
    public void calculateDailyData(LocalDate localDate) {
        String dataStats = localDate.toString();

        if(fireDailyDataRepo.findFirstByStatsDate(dataStats) != null) {
            log.info("====== 火系统日数据统计已存在：{}", dataStats);
            return ;
        }

        FireDailyData fireDailyData = new FireDailyData();
        fireDailyData.setStatsDate(localDate.toString());
        fireDailyData.setStatsType(FireDailyDataRepository.STATS_TYPE_DAY);

        // 获取一天的开始和结束时间
        long start = LocalDateTime.of(localDate, LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = LocalDateTime.of(localDate, LocalTime.MAX).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        log.info("startTime: {} ;endTime :{}" ,start,end);

        saveFireDailyData(fireDailyData,start,end);
    }

    @Override
    public void calculateMonthData(LocalDate localDate) {
        String dataStats = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        if(fireDailyDataRepo.findFirstByStatsDate(dataStats) != null) {
            log.info("====== 火系统月数据统计已存在：{}", dataStats);
            return ;
        }

        FireDailyData fireDailyData = new FireDailyData();
        fireDailyData.setStatsDate(dataStats);
        fireDailyData.setStatsType(FireDailyDataRepository.STATS_TYPE_MONTH);

        // 获取当月的的开始和结束时间
        long start = LocalDateTime.of(localDate.with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = LocalDateTime.of(localDate, LocalTime.MAX).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        log.info("startTime: {} ;endTime :{}" ,start,end);

        saveFireDailyData(fireDailyData,start,end);

    }

    private void saveFireDailyData(FireDailyData fireDailyData,long start,long end){
        fireDailyData.setCountFire(fireEventService.countByFireEventStatsBo(new FireEventStatsBo(start,end,FireEventRepository.EVENT_TYPE_FIRE,FireEventRepository.EVENT_STATUS_ADD)));
        fireDailyData.setCountFault(fireEventService.countByFireEventStatsBo(new FireEventStatsBo(start,end,FireEventRepository.EVENT_TYPE_FAULT,FireEventRepository.EVENT_STATUS_ADD)));
        fireDailyData.setCountShield(fireEventService.countByFireEventStatsBo(new FireEventStatsBo(start,end,FireEventRepository.EVENT_TYPE_SHIELD,FireEventRepository.EVENT_STATUS_ADD)));
        fireDailyData.setCountAll(fireEventService.countByFireEventStatsBo(new FireEventStatsBo(start,end,null,FireEventRepository.EVENT_STATUS_ADD)));
        fireDailyData.setCountOther();
        fireDailyData.setCreateTime(LocalDateTime.now());
        fireDailyDataRepo.save(fireDailyData);
    }

    @Override
    public List<FireDailyData> findByStatsTypeAndStatsDateBetween(int statsType, String start, String end) {
        return fireDailyDataRepo.findByStatsTypeAndStatsDateBetween(statsType,start,end);
    }
}
