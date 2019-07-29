package com.honeywell.fireiot.service;


import com.honeywell.fireiot.entity.Holiday;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public interface HolidayService {


    Boolean addHoliday(Holiday holiday) throws SchedulerException;

    Boolean deleteHoliday(Long id) throws SchedulerException;

    Page<Holiday> findByCondition(Specification<Holiday> specification);


    Boolean update(Holiday holiday) throws SchedulerException;

}