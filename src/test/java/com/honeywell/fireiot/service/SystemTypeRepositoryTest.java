package com.honeywell.fireiot.service;

import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.entity.SystemType;
import com.honeywell.fireiot.repository.SystemTypeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SystemTypeRepositoryTest {
    @Autowired
    SystemTypeRepository systemTypeRepository;

    @Test
    public void init(){
        SystemType systemType = new SystemType();
        systemType.setName("消防报警系统");
        List<DeviceType> deviceTypes = new ArrayList<>();
        DeviceType deviceType1 = new DeviceType();
        deviceType1.setName("气灭主机");
        deviceType1.setDescription("气灭主机");
        deviceType1.setSystemType(systemType);

        DeviceType deviceType2 = new DeviceType();
        deviceType2.setName("消火栓");
        deviceType2.setDescription("消火栓");
        deviceType2.setSystemType(systemType);

        DeviceType deviceType3 = new DeviceType();
        deviceType3.setName("传统感烟");
        deviceType3.setDescription("传统感烟");
        deviceType3.setSystemType(systemType);

        DeviceType deviceType4 = new DeviceType();
        deviceType4.setName("回路");
        deviceType4.setDescription("回路");
        deviceType4.setSystemType(systemType);

        DeviceType deviceType5 = new DeviceType();
        deviceType5.setName("卷帘门");
        deviceType5.setDescription("卷帘门");
        deviceType5.setSystemType(systemType);

        DeviceType deviceType6 = new DeviceType();
        deviceType6.setName("火焰探测器");
        deviceType6.setDescription("火焰探测器");
        deviceType6.setSystemType(systemType);

        DeviceType deviceType7 = new DeviceType();
        deviceType7.setName("手报");
        deviceType7.setDescription("手报");
        deviceType7.setSystemType(systemType);

        DeviceType deviceType8 = new DeviceType();
        deviceType8.setName("排烟阀");
        deviceType8.setDescription("排烟阀");
        deviceType8.setSystemType(systemType);

        DeviceType deviceType9 = new DeviceType();
        deviceType9.setName("电梯");
        deviceType9.setDescription("电梯");
        deviceType9.setSystemType(systemType);

        DeviceType deviceType10 = new DeviceType();
        deviceType10.setName("温感");
        deviceType10.setDescription("温感");
        deviceType10.setSystemType(systemType);

        DeviceType deviceType11 = new DeviceType();
        deviceType11.setName("控制器");
        deviceType11.setDescription("控制器");
        deviceType11.setSystemType(systemType);

        DeviceType deviceType12 = new DeviceType();
        deviceType12.setName("细水雾");
        deviceType12.setDescription("细水雾");
        deviceType12.setSystemType(systemType);

        DeviceType deviceType13 = new DeviceType();
        deviceType13.setName("压力开关");
        deviceType13.setDescription("压力开关");
        deviceType13.setSystemType(systemType);

        DeviceType deviceType14 = new DeviceType();
        deviceType14.setName("排烟风机");
        deviceType14.setDescription("排烟风机");
        deviceType14.setSystemType(systemType);

        DeviceType deviceType15 = new DeviceType();
        deviceType15.setName("模块");
        deviceType15.setDescription("模块");
        deviceType15.setSystemType(systemType);

        DeviceType deviceType16 = new DeviceType();
        deviceType16.setName("未知");
        deviceType16.setDescription("未知");
        deviceType16.setSystemType(systemType);

        DeviceType deviceType17 = new DeviceType();
        deviceType17.setName("泵");
        deviceType17.setDescription("泵");
        deviceType17.setSystemType(systemType);

        DeviceType deviceType18 = new DeviceType();
        deviceType18.setName("广播");
        deviceType18.setDescription("广播");
        deviceType18.setSystemType(systemType);

        DeviceType deviceType19 = new DeviceType();
        deviceType19.setName("水流指示器");
        deviceType19.setDescription("水流指示器");
        deviceType19.setSystemType(systemType);

        DeviceType deviceType20 = new DeviceType();
        deviceType20.setName("切电");
        deviceType20.setDescription("切电");
        deviceType20.setSystemType(systemType);

        DeviceType deviceType21 = new DeviceType();
        deviceType21.setName("烟感");
        deviceType21.setDescription("烟感");
        deviceType21.setSystemType(systemType);

        DeviceType deviceType22 = new DeviceType();
        deviceType22.setName("信号阀");
        deviceType22.setDescription("信号阀");
        deviceType22.setSystemType(systemType);

        DeviceType deviceType23 = new DeviceType();
        deviceType23.setName("警铃");
        deviceType23.setDescription("警铃");
        deviceType23.setSystemType(systemType);

        deviceTypes.add(deviceType1);
        deviceTypes.add(deviceType2);
        deviceTypes.add(deviceType3);
        deviceTypes.add(deviceType4);
        deviceTypes.add(deviceType5);
        deviceTypes.add(deviceType6);
        deviceTypes.add(deviceType7);
        deviceTypes.add(deviceType8);
        deviceTypes.add(deviceType9);
        deviceTypes.add(deviceType10);
        deviceTypes.add(deviceType11);
        deviceTypes.add(deviceType12);
        deviceTypes.add(deviceType13);
        deviceTypes.add(deviceType14);
        deviceTypes.add(deviceType15);
        deviceTypes.add(deviceType16);
        deviceTypes.add(deviceType17);
        deviceTypes.add(deviceType18);
        deviceTypes.add(deviceType19);
        deviceTypes.add(deviceType20);
        deviceTypes.add(deviceType21);
        deviceTypes.add(deviceType22);
        deviceTypes.add(deviceType23);
        systemType.setDeviceTypes(deviceTypes);

        systemTypeRepository.save(systemType);

        systemType = new SystemType();
        systemType.setName("消防水系统");
        List<DeviceType> waterDeviceTypes = new ArrayList<>();

        DeviceType waterDeviceType1 = new DeviceType();
        waterDeviceType1.setName("流量计传感器");
        waterDeviceType1.setDescription("WATER_SYS_FLOWMETER");
        waterDeviceType1.setSystemType(systemType);

        DeviceType waterDeviceType2 = new DeviceType();
        waterDeviceType2.setName("压力传感器");
        waterDeviceType2.setDescription("WATER_SYS_PRESSURE_COLLECT");
        waterDeviceType2.setSystemType(systemType);

        DeviceType waterDeviceType3 = new DeviceType();
        waterDeviceType3.setName("温湿度传感器");
        waterDeviceType3.setDescription("WATER_SYS_TEMPERATURE_HUMIDITY");
        waterDeviceType3.setSystemType(systemType);

        DeviceType waterDeviceType4 = new DeviceType();
        waterDeviceType4.setName("液位传感器");
        waterDeviceType4.setDescription("WATER_SYS_LIQUID_LEVEL");
        waterDeviceType4.setSystemType(systemType);

        DeviceType waterDeviceType5 = new DeviceType();
        waterDeviceType5.setName("水浸传感器");
        waterDeviceType5.setDescription("WATER_SYS_WATER_IMMERSION");
        waterDeviceType5.setSystemType(systemType);

        DeviceType waterDeviceType6 = new DeviceType();
        waterDeviceType6.setName("电池检测传感器");
        waterDeviceType6.setDescription("WATER_SYS_BATTERY");
        waterDeviceType6.setSystemType(systemType);

        waterDeviceTypes.add(waterDeviceType1);
        waterDeviceTypes.add(waterDeviceType2);
        waterDeviceTypes.add(waterDeviceType3);
        waterDeviceTypes.add(waterDeviceType4);
        waterDeviceTypes.add(waterDeviceType5);
        waterDeviceTypes.add(waterDeviceType6);

        systemType.setDeviceTypes(waterDeviceTypes);
        systemTypeRepository.save(systemType);
    }
}
