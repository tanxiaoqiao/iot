package com.honeywell.fireiot.service;

import com.honeywell.fireiot.fire.entity.MTGateway;
import com.honeywell.fireiot.fire.service.MTGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author ： YingZhang
 * @Description:  数据库的初始化操作
 * @Date : Create in 10:27 AM 12/27/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MTGatewayServiceTest {

    @Autowired
    MTGatewayService mtGatewayService;

    @Test
    public void save() {
        MTGateway mtGateway =new MTGateway();
        mtGateway.setPointSleepTime(200);
        mtGateway.setPort(502);
        mtGateway.setLimit(120);
        mtGateway.setIntervalSleepTime(3000);
        mtGateway.setIp("127.0.0.1");
        mtGatewayService.save(mtGateway);

//        mtGateway =new MTGateway();
//        mtGateway.setPointSleepTime(200);
//        mtGateway.setPort(502);
//        mtGateway.setLimit(120);
//        mtGateway.setIntervalSleepTime(3000);
//        mtGateway.setIp("127.0.0.2");
//        mtGatewayService.save(mtGateway);
//
//        mtGateway =new MTGateway();
//        mtGateway.setPointSleepTime(200);
//        mtGateway.setPort(502);
//        mtGateway.setLimit(120);
//        mtGateway.setIntervalSleepTime(3000);
//        mtGateway.setIp("127.0.0.3");
//        mtGatewayService.save(mtGateway);
    }

    @Test
    public void delete() {
    }

    @Test
    public void deleteById() {
    }

    @Test
    public void findById() {
    }

    @Test
    public void findPage() {
    }

    @Test
    public void findPage1() {
    }

    @Test
    public void toDto() {
    }
}