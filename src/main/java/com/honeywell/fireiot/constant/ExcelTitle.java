package com.honeywell.fireiot.constant;

import java.util.Arrays;
import java.util.List;


public class ExcelTitle {

    public static final String SYSTEM_NAME = "系统类型";
    public static final String DEVICE_TYPE ="设备类型";
    public static final String LOCATION="安装位置";
    public static final String DEVICE_LABEL="设备标签";
    public static final String DEVICE_ID="设备ID";
    public static final String DEVICE_NO="设备编号";
    public static final String BRAND_NAME="品牌";
    public static final String DATE_PRODUCTION="出厂日期";
    public static final String DATE_INSTALLATION="安装日期";
    public static final String DATE_COMMISSIONING="启用日期";
    public static final String DESCRIPTION="描述";
    public static final String LIFETIME="设计寿命";

    public static final List<String> DEVICE_TITLE_LIST = Arrays.asList(DEVICE_ID,DEVICE_NO, DEVICE_LABEL
            , DEVICE_TYPE, LOCATION
            , BRAND_NAME, DATE_PRODUCTION,DATE_INSTALLATION,DATE_COMMISSIONING,LIFETIME,DESCRIPTION);


    public static final String  LOG_TYPE="操作类型";

    public static final String  LOG_CONTENT="操作内容";

    public static final String LOG_OPERATOR="操作人";

    public static final String LOG_TIME="操作时间";

    public static final List<String> LOG_TITLE_LIST=Arrays.asList(LOG_TYPE, LOG_CONTENT
            , LOG_OPERATOR, LOG_TIME);

    public static final String FIRE_EVENT_DEVICENO="设备编号";
    public static final String FIRE_EVENT_DEVICELABEL="设备标签";
    public static final String FIRE_EVENT_DEVICETYPE="设备类型";
    public static final String FIRE_EVENT_BUILDING="楼栋";
    public static final String FIRE_EVENT_FLOOR="楼层";
    public static final String FIRE_EVENT_TYPE="事件类型";
    public static final String FIRE_EVENT_STATUS="事件状态";
    public static final String FIRE_EVENT_TIME="事件发生时间";

    public static final List<String> FIRE_EVENT_TITLE_LIST=Arrays.asList(FIRE_EVENT_DEVICENO,FIRE_EVENT_DEVICELABEL,FIRE_EVENT_DEVICETYPE,
            FIRE_EVENT_BUILDING ,FIRE_EVENT_FLOOR,FIRE_EVENT_TYPE,FIRE_EVENT_STATUS,FIRE_EVENT_TIME);

    public static final String FIRE_DEVICEID="设备ID";
    public static final String FIRE_DEVICENO="设备编号";
    public static final String FIRE_DEVICELABEL="设备标签";
    public static final String FIRE_DEVICETYPE="设备类型";
    public static final String FIRE__BUILDING="楼栋";
    public static final String FIRE_FLOOR="楼层";
    public static final String FIRE_DEVICESTATUS="设备状态";
    public static final String FIRE_ZONE="区号";
    public static final String FIRE_POINT="点号";
    public static final List<String> FIRE_TITLE_LIST=Arrays.asList(FIRE_DEVICEID,FIRE_DEVICENO,FIRE_DEVICELABEL,FIRE_DEVICETYPE,FIRE_DEVICESTATUS,FIRE__BUILDING,FIRE_FLOOR,FIRE_ZONE,FIRE_POINT);


    public static final String WATER_EVENT_DEVICENO="设备编号";
    public static final String WATER_EVENT_DEVICELABEL="设备标签";
    public static final String WATER_EVENT_DEVICETYPE="设备属性";
    public static final String WATER_EVENT_MAX="最大值";
    public static final String WATER_EVENT_MIN="最小值";
    public static final String WATER_EVENT_CURRENT="当前值";
    public static final String WATER_EVENT_UNIT="单位";
    public static final String WATER_EVENT_TYPE="事件类型";
    public static final String WATER_EVENT_TIME="事件发生时间";

    public static final List<String> WATER_EVENT_TITLE_LIST=Arrays.asList(WATER_EVENT_DEVICENO,WATER_EVENT_DEVICELABEL,WATER_EVENT_DEVICETYPE,
            WATER_EVENT_MAX ,WATER_EVENT_MIN,WATER_EVENT_CURRENT,WATER_EVENT_UNIT,WATER_EVENT_TYPE,WATER_EVENT_TIME);


    public static final String WATER_DEVICENO="设备编号";
    public static final String WATER_DEVICELABEL="设备标签";
    public static final String WATER_DEVICETYPE="设备类型";
    public static final String WATER_DEVICESTATUS="设备状态";
    public static final String WATER_LOCATION="所处位置";

    public static final List<String> WATER_TITLE_LIST=Arrays.asList(WATER_DEVICENO,WATER_DEVICELABEL,WATER_DEVICETYPE,WATER_DEVICESTATUS,WATER_LOCATION);
}
