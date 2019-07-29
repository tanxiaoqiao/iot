package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.entity.Holiday;
import com.honeywell.fireiot.repository.HolidayRepository;
import com.honeywell.fireiot.service.HolidayService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.calendar.AnnualCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service
public class HolidayServiceImpl implements HolidayService {


    private static Logger logger = LoggerFactory.getLogger(HolidayServiceImpl.class);

    private static final String DEFINE_HOLIDAY = "DEFINE_HOLIDAY";

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    private Scheduler scheduler;


    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean addHoliday(Holiday holiday) throws SchedulerException {
        holidayRepository.save(holiday);
        updateCalendar();
        return true;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean deleteHoliday(Long id) throws SchedulerException {
        holidayRepository.deleteById(id);
        updateCalendar();
        return true;
    }

    @Override
    public Page<Holiday> findByCondition(Specification<Holiday> specification) {
        return holidayRepository.findAll(specification, JpaUtils.getPageRequest());
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean update(Holiday holiday) throws SchedulerException {
        holidayRepository.save(holiday);
        updateCalendar();
        return true;
    }


    private void updateCalendar() throws SchedulerException {
        List<Holiday> holidays = holidayRepository.findAll();
        Set<Calendar> set = new HashSet<>();
        holidays.forEach(h -> {
            //定义一个接受时间的集合
            Calendar start = Calendar.getInstance();
            // 设置初始时间
            start.setTime(h.getStartTime());
            set.add(start);
            Calendar end = Calendar.getInstance();
            // 设置结束时间
            end.setTime(h.getEndTime());
            // 测试此日期是否在指定日期之后
            Calendar add = Calendar.getInstance();
            while (start.getTime().before(end.getTime())) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                start.add(Calendar.DAY_OF_YEAR, 1);
                add.setTime(start.getTime());
                set.add(add);
            }
            set.add(end);
        });
        AnnualCalendar excluseDay = new AnnualCalendar();
        ArrayList<Calendar> calendars = new ArrayList<>(set);
        excluseDay.setDaysExcluded(calendars);
        //注册日历
        scheduler.addCalendar(DEFINE_HOLIDAY, excluseDay, true, true);
    }

}





