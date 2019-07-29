package com.honeywell.fireiot.fire.service;

import com.honeywell.fireiot.fire.dto.FireDailyDataDto;
import com.honeywell.fireiot.fire.entity.FireDailyData;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date :
 */
public interface FireDailyDataService {

    void save(FireDailyData entity);

    void delete(FireDailyData t);

    void deleteById(Long id);

    Optional<FireDailyData> findById(Long id);

    Page<FireDailyData> findPage();

    Pagination findPage(Specification<FireDailyData> specification);

    FireDailyDataDto toDto(FireDailyData entity);

    List<FireDailyData> findAll();

    void calculateDailyData(LocalDate date);

    void calculateMonthData(LocalDate date);

    List<FireDailyData> findByStatsTypeAndStatsDateBetween(int statsType,String start,String end);

}
