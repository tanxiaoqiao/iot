package com.honeywell.fireiot.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 10:18 AM 3/15/2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FireDeviceServiceTest {

    @Autowired
    FireDeviceService fireDeviceService;

    @Test
    public void countDeviceByStatus() {
        //System.out.println(fireDeviceService.countDeviceByStatus());
    }

    @Test
    public void countDeviceByDeviceType() {
        //System.out.println(fireDeviceService.countDeviceByDeviceType());
    }
}