package com.honeywell.fireiot.water.util;

import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.water.entity.WaterField;
import com.honeywell.fireiot.water.sensor.constant.WaterSensorEnum;
import com.honeywell.fireiot.water.sensor.constant.WaterSensorItems;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author: xiaomingCao
 * @date: 2018/12/29
 */
public class WaterFieldUtil {

    public static List<WaterField> getWaterFields(DeviceType deviceType, String deviceNo, String eui, Double[] ... maxmin){

        String ename = deviceType.getDescription();

        WaterSensorEnum waterSensorEnum = WaterSensorEnum.valueOf(ename);


        Double max1 = null;
        Double min1 = null;
        if(Objects.nonNull(maxmin) && maxmin.length >= 1){
            max1 = maxmin[0][0];
            min1 = maxmin[0][1];
        }

        Double max2 = null;
        Double min2 = null;
        if(Objects.nonNull(maxmin) && maxmin.length >= 2){
            max2 = maxmin[1][0];
            min2 = maxmin[1][1];
        }

        switch (waterSensorEnum){
            case WATER_SYS_LIQUID_LEVEL:
                return Arrays.asList(
                        liquidLevel(deviceNo, eui, max1, min1),
                        electricPressure(deviceNo, eui),
                        electricPressureAlarm(deviceNo, eui)
                );
            case WATER_SYS_PRESSURE_COLLECT:
                return Arrays.asList(
                        pressure(deviceNo, eui, max1, min1),
                        electricPressure(deviceNo, eui),
                        electricPressureAlarm(deviceNo, eui)
                );
            case WATER_SYS_WATER_IMMERSION:
                return Arrays.asList(
                        waterExistAlarm(deviceNo, eui),
                        waterOverflowAlarm(deviceNo, eui),
                        electricPressure(deviceNo, eui),
                        electricPressureAlarm(deviceNo, eui)
                );
            case WATER_SYS_TEMPERATURE_HUMIDITY:
                return Arrays.asList(
                        temperature(deviceNo, eui, max1, min1),
                        humidity(deviceNo, eui, max2, min2),
                        electricPressure(deviceNo, eui),
                        electricPressureAlarm(deviceNo, eui)
                );

            case WATER_SYS_BATTERY:
                return Arrays.asList(
                        electricPressure(deviceNo, eui),
                        batteryTemperature(deviceNo, eui, null, null),
                        batteryTemperatureAlarm(deviceNo, eui),
                        internalResistance(deviceNo, eui, null, null),
                        internalResistanceAlarm(deviceNo, eui),
                        electricCurrent(deviceNo,eui, null, null),
                        environmentTemperature1(deviceNo, eui, null, null),
                        environmentTemperature2(deviceNo, eui, null, null)
                );
            case WATER_SYS_FLOWMETER:
                return Arrays.asList(
                        flow(deviceNo, eui)
                );
            default:
                return Collections.emptyList();
        }

    }


    private static WaterField liquidLevel(String deviceNo, String eui, Double max, Double min){
        WaterField liquidLevel = new WaterField();
        liquidLevel.setName(WaterSensorItems.LIQUID_LEVEL);
        liquidLevel.setTitle("液位");
        liquidLevel.setDeviceNo(deviceNo);
        liquidLevel.setEui(eui);
        liquidLevel.setUnitName("厘米");
        liquidLevel.setUnitSymbol("cm");

        if(Objects.nonNull(max) && Objects.nonNull(min)){
            liquidLevel.setCheckRange(1);
            liquidLevel.setMax(max);
            liquidLevel.setMin(min);
        }else{
            // 设备默认值
            liquidLevel.setCheckRange(1);
            liquidLevel.setMax(400);
            liquidLevel.setMin(100);
        }
        return liquidLevel;
    }





    private static WaterField electricPressure(String deviceNo, String eui){
        WaterField electricPressure = new WaterField();
        electricPressure.setUnitSymbol("V");
        electricPressure.setUnitName("伏");
        electricPressure.setEui(eui);
        electricPressure.setDeviceNo(deviceNo);
        electricPressure.setTitle("电池电压");
        electricPressure.setName(WaterSensorItems.ELECTRIC_PRESSURE);
        return electricPressure;
    }


    private static WaterField electricPressureAlarm(String deviceNo, String eui){
        WaterField electricPressureAlarm = new WaterField();
        electricPressureAlarm.setUnitSymbol("");
        electricPressureAlarm.setUnitName("");
        electricPressureAlarm.setEui(eui);
        electricPressureAlarm.setDeviceNo(deviceNo);
        electricPressureAlarm.setTitle("电池低电压");
        electricPressureAlarm.setName(WaterSensorItems.ELECTRIC_PRESSURE_ALARM);
        electricPressureAlarm.setCheckTrueFalse(1);
        return electricPressureAlarm;
    }


    private static WaterField pressure(String deviceNo, String eui, Double max, Double min){
        WaterField pressure = new WaterField();
        pressure.setName(WaterSensorItems.PRESSURE);
        pressure.setTitle("压力");
        pressure.setDeviceNo(deviceNo);
        pressure.setEui(eui);
        pressure.setUnitName("千帕");
        pressure.setUnitSymbol("Kpa");

        if(Objects.nonNull(max) && Objects.nonNull(min)){
            pressure.setCheckRange(1);
            pressure.setMax(max);
            pressure.setMin(min);
        }else{
            // 设备默认值
            pressure.setCheckRange(1);
            pressure.setMax(2500);
            pressure.setMin(500);
        }

        return pressure;
    }



    private static WaterField temperature(String deviceNo, String eui, Double max, Double min){
        WaterField temperature = new WaterField();
        temperature.setName(WaterSensorItems.TEMPERATURE);
        temperature.setTitle("温度");
        temperature.setDeviceNo(deviceNo);
        temperature.setEui(eui);
        temperature.setUnitName("摄氏度");
        temperature.setUnitSymbol("℃");

        if(Objects.nonNull(max) && Objects.nonNull(min)){
            temperature.setCheckRange(1);
            temperature.setMax(max);
            temperature.setMin(min);
        }

        return temperature;
    }


    private static WaterField humidity(String deviceNo, String eui, Double max, Double min){
        WaterField humidity = new WaterField();
        humidity.setName(WaterSensorItems.HUMIDITY);
        humidity.setTitle("湿度");
        humidity.setDeviceNo(deviceNo);
        humidity.setEui(eui);
        humidity.setUnitName("百分比");
        humidity.setUnitSymbol("%");
        if(Objects.nonNull(max) && Objects.nonNull(min)){
            humidity.setCheckRange(1);
            humidity.setMax(max);
            humidity.setMin(min);
        }
        return humidity;
    }


    private static WaterField waterOverflowAlarm(String deviceNo, String eui){
        WaterField waterOverflowAlarm = new WaterField();
        waterOverflowAlarm.setUnitSymbol("");
        waterOverflowAlarm.setUnitName("");
        waterOverflowAlarm.setEui(eui);
        waterOverflowAlarm.setDeviceNo(deviceNo);
        waterOverflowAlarm.setTitle("水浸告警");
        waterOverflowAlarm.setName(WaterSensorItems.WATER_OVERFLOW_ALARM);
        waterOverflowAlarm.setCheckTrueFalse(1);
        return waterOverflowAlarm;
    }


    private static WaterField waterExistAlarm(String deviceNo, String eui){
        WaterField waterExistAlarm = new WaterField();
        waterExistAlarm.setUnitSymbol("");
        waterExistAlarm.setUnitName("");
        waterExistAlarm.setEui(eui);
        waterExistAlarm.setDeviceNo(deviceNo);
        waterExistAlarm.setTitle("无水告警");
        waterExistAlarm.setName(WaterSensorItems.WATER_EXIST_ALARM);
        waterExistAlarm.setCheckTrueFalse(1);
        return waterExistAlarm;
    }


    private static WaterField batteryTemperatureAlarm(String deviceNo, String eui){
        WaterField temperatureAlarm = new WaterField();
        temperatureAlarm.setUnitSymbol("");
        temperatureAlarm.setUnitName("");
        temperatureAlarm.setEui(eui);
        temperatureAlarm.setDeviceNo(deviceNo);
        temperatureAlarm.setTitle("电池温度告警");
        temperatureAlarm.setName(WaterSensorItems.BATTERY_TEMPERATURE_ALARM);
        temperatureAlarm.setCheckTrueFalse(1);
        return temperatureAlarm;
    }


    private static WaterField internalResistance(String deviceNo, String eui, Double max, Double min){
        WaterField internalResistance = new WaterField();
        internalResistance.setName(WaterSensorItems.INTERNAL_RESISTANCE);
        internalResistance.setTitle("单体内阻");
        internalResistance.setDeviceNo(deviceNo);
        internalResistance.setEui(eui);
        internalResistance.setUnitName("电阻");
        internalResistance.setUnitSymbol("µΩ");
        if(Objects.nonNull(max) && Objects.nonNull(min)){
            internalResistance.setCheckRange(1);
            internalResistance.setMax(max);
            internalResistance.setMin(min);
        }
        return internalResistance;
    }


    private static WaterField internalResistanceAlarm(String deviceNo, String eui){
        WaterField alarm = new WaterField();
        alarm.setUnitSymbol("");
        alarm.setUnitName("");
        alarm.setEui(eui);
        alarm.setDeviceNo(deviceNo);
        alarm.setTitle("内阻告警");
        alarm.setName(WaterSensorItems.INTERNAL_RESISTANCE_ALARM);
        alarm.setCheckTrueFalse(1);
        return alarm;
    }


    public static WaterField electricCurrent(String deviceNo, String eui, Double max, Double min){
        WaterField field = new WaterField();
        field.setName(WaterSensorItems.ELECTRIC_CURRENT);
        field.setTitle("充放电电流");
        field.setDeviceNo(deviceNo);
        field.setEui(eui);
        field.setUnitName("安");
        field.setUnitSymbol("A");

        if(Objects.nonNull(max) && Objects.nonNull(min)){
            field.setCheckRange(1);
            field.setMax(max);
            field.setMin(min);
        }

        return field;
    }


    private static WaterField batteryTemperature(String deviceNo, String eui, Double max, Double min){
        WaterField temperature1 = new WaterField();
        temperature1.setName(WaterSensorItems.BATTERY_TEMPERATURE);
        temperature1.setTitle("电池温度");
        temperature1.setDeviceNo(deviceNo);
        temperature1.setEui(eui);
        temperature1.setUnitName("摄氏度");
        temperature1.setUnitSymbol("℃");

        if(Objects.nonNull(max) && Objects.nonNull(min)){
            temperature1.setCheckRange(1);
            temperature1.setMax(max);
            temperature1.setMin(min);
        }

        return temperature1;
    }

    private static WaterField environmentTemperature1(String deviceNo, String eui, Double max, Double min){
        WaterField temperature1 = new WaterField();
        temperature1.setName(WaterSensorItems.ENV_TEMPERATURE_1);
        temperature1.setTitle("环境温度1");
        temperature1.setDeviceNo(deviceNo);
        temperature1.setEui(eui);
        temperature1.setUnitName("摄氏度");
        temperature1.setUnitSymbol("℃");

        if(Objects.nonNull(max) && Objects.nonNull(min)){
            temperature1.setCheckRange(1);
            temperature1.setMax(max);
            temperature1.setMin(min);
        }

        return temperature1;
    }


    private static WaterField environmentTemperature2(String deviceNo, String eui, Double max, Double min){
        WaterField temperature2 = new WaterField();
        temperature2.setName(WaterSensorItems.ENV_TEMPERATURE_2);
        temperature2.setTitle("环境温度2");
        temperature2.setDeviceNo(deviceNo);
        temperature2.setEui(eui);
        temperature2.setUnitName("摄氏度");
        temperature2.setUnitSymbol("℃");

        if(Objects.nonNull(max) && Objects.nonNull(min)){
            temperature2.setCheckRange(1);
            temperature2.setMax(max);
            temperature2.setMin(min);
        }

        return temperature2;
    }


    private static WaterField flow(String deviceNo, String eui){
        WaterField field = new WaterField();
        field.setUnitName("立方/分");
        field.setUnitSymbol("m3/m");
        field.setDeviceNo(deviceNo);
        field.setEui(eui);
        field.setName(WaterSensorItems.FLOW);
        field.setTitle("瞬时流量");
        return field;
    }
}
