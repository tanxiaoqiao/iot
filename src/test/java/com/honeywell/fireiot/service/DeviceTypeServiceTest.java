package com.honeywell.fireiot.service;

import com.honeywell.fireiot.entity.DeviceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Author ： YingZhang
 * @Description:  数据库的初始化操作
 * @Date : Create in 10:27 AM 12/27/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceTypeServiceTest {

    @Autowired
    DeviceTypeService deviceTypeService;

    @Test
    public void save() {
//        DeviceType deviceType = new DeviceType();
//        deviceType.setEName("O-Detector");
//        deviceType.setCName("感烟探测器");
//        deviceType.setType("Detector");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("Analogue/AD");
//        deviceType.setCName("探测区");
//        deviceType.setType("Detector");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("O2T-Detector");
//        deviceType.setCName("双光电烟温复合探测器");
//        deviceType.setType("Detector");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("RoR Temperature Detector");
//        deviceType.setCName("差温探测器");
//        deviceType.setType("Detector");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("Analogue/MCP");
//        deviceType.setCName("手报区");
//        deviceType.setType("Manual");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("MCP");
//        deviceType.setCName("手报");
//        deviceType.setType("Manual");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("Standard/MCP");
//        deviceType.setCName("普通手报");
//        deviceType.setType("Manual");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("Analogue Transponder");
//        deviceType.setCName("模块区");
//        deviceType.setType("Module");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("4 Zone/2 Relay");
//        deviceType.setCName("4入2出");
//        deviceType.setType("Module");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("Transponder");
//        deviceType.setCName("模块");
//        deviceType.setType("Module");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("Standard/AD");
//        deviceType.setCName("普通区");
//        deviceType.setType("Module");
//        deviceTypeService.save(deviceType);
//
//        deviceType = new DeviceType();
//        deviceType.setEName("Basic Module(CF Relay)");
//        deviceType.setCName("主板模块");
//        deviceType.setType("Module");
//        deviceTypeService.save(deviceType);







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