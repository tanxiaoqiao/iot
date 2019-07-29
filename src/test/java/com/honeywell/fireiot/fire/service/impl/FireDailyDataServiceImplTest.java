package com.honeywell.fireiot.fire.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.honeywell.fireiot.dto.PushBody;
import com.honeywell.fireiot.fire.repository.FireDailyDataRepository;
import com.honeywell.fireiot.fire.service.FireDailyDataService;
import com.honeywell.fireiot.job.WorkorderMsgEndJob;
import com.honeywell.fireiot.utils.HttpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;


/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:30 PM 3/20/2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FireDailyDataServiceImplTest {

    @Autowired
    FireDailyDataService fireDailyDataService;

    @Autowired
    FireDailyDataRepository fireDailyDataRepository;

    @Test
    public void calculateDailyData() {

        fireDailyDataService.calculateDailyData(LocalDate.now()
        );
    }

    @Test
    public void test1() {
        System.out.println(fireDailyDataRepository.findByStatsTypeAndStatsDateBetween(0, "2019-02-16", "2019-03-20"));
        System.out.println(fireDailyDataRepository.findByStatsTypeAndStatsDateBetween(0, "2019-02-16", "2019-03-20").size());
    }

    @Test
    public void test2() {
    }


}
