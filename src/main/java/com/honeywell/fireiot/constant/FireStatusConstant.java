package com.honeywell.fireiot.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 10:23 AM 12/27/2018
 */

public class FireStatusConstant {

    public static final int INSULATE_INDEX = 2;
    public static final int  ALARM_INDEX = 1;
    public static final int  TROUBLE_INDEX = 0;
    public static final String  INSULATE =  "隔离";//隔离
    public static final String  ALARM = "报警";//报警
    public static final String  TROUBLE ="故障";//故障


    /************************************多态设备**********************************************/

    // 回路号
    public static final String  LOOP_NAME = "回路";//回路

    // 英文
//    public static  final String LOOP_RESETING = "Reseting";
//    public static final String LOOP_DISABLE = "Disable";
//    public static final String LOOP_TROUBLE = "Trouble";
//    public static final String LOOP_FIREALARM = "Fire Alarm";
    public static  final String LOOP_RESETING = "复位";
    public static final String LOOP_DISABLE = "隔离";
    public static final String LOOP_TROUBLE = "故障";
    public static final String LOOP_FIREALARM = "火警";

    public static final int LOOP_RESETING_INDEX = 3;
    public static final int LOOP_DISABLE_INDEX = 2;
    public static final int LOOP_TROUBLE_INDEX = 1;
    public static final int LOOP_FIREALARM_INDEX = 0;


    //控制器
    public static final String CONTROL_NAME = "控制器";
    public static final int CONTROL_INDEXONE = 1;
    public static final int CONTROL_INDEXTWO = 2;

    //英文名字
//    public static final String CONTROL_OPERATING_LEVEL_1 = "OPERATING LEVEL_1";
//    public static final String CONTROL_OPERATING_LEVEL_2 = "OPERATING LEVEL_2";
//    public static final String CONTROL_OPERATING_LEVEL_3 = "OPERATING LEVEL_3";
//    public static final String CONTROL_OPERATING_LEVEL_4 = "OPERATING LEVEL_4";
//    public static final String CONTROL_OPERATING_LEVEL_5 = "OPERATING LEVEL_5";

    //中文名字
    public static final String CONTROL_OPERATING_LEVEL_1 = "LEVEL 1";
    public static final String CONTROL_OPERATING_LEVEL_2 = "LEVEL 2";
    public static final String CONTROL_OPERATING_LEVEL_3 = "LEVEL 3";
    public static final String CONTROL_OPERATING_LEVEL_4 = "LEVEL 4";
    public static final String CONTROL_OPERATING_LEVEL_5 = "LEVEL 5";

    public static final int CONTROL_OPERATING_LEVLEL_1_INDEX = 15;
    public static final int CONTROL_OPERATING_LEVLEL_2_INDEX = 14;
    public static final int CONTROL_OPERATING_LEVLEL_3_INDEX = 13;
    public static final int CONTROL_OPERATING_LEVLEL_4_INDEX = 12;
    public static final int CONTROL_OPERATING_LEVLEL_5_INDEX = 11;

    //英文名
//    public static final String CONTROL_RESETING = "Reseting(OZ_BMZ_RESET_IS RUNNING)";
//    public static final String CONTROL_PANEL_TEST_MODE = "Panel Test Mode(OZ_TEST OPERATION)";
//    public static final String CONTROL_KEY_SWITCH_FREE = "Key Switch Free(OZ_KEY SWITCH_FREE)";
//    public static final String CONTROL_COVER_CONTACT_OPEN = "Cover Contact Open";
    public static final String CONTROL_RESETING = "复位";
    public static final String CONTROL_PANEL_TEST_MODE = "测试模式";
    public static final String CONTROL_KEY_SWITCH_FREE = "钥匙开";
    public static final String CONTROL_COVER_CONTACT_OPEN = "面板开";


    public static final int CONTROL_RESETING_INDEX = 3;
    public static final int CONTROL_PANEL_TEST_MODE_INDEX = 2;
    public static final int CONTROL_KEY_SWITCH_FREE_INDEX = 1;
    public static final int CONTROL_COVER_CONTACT_OPEN_INDEX = 0;

    //英文名字
//    public static final String CONTROL_CPU_FAIL = "CPU Fail";
//    public static final String CONTROL_NETWORK_TROUBLE = "Network Trouble";
//    public static final String CONTROL_GROUND_FAULT = "Ground Fault";
//    public static final String CONTROL_POWER_SUPPLY_TROUBLE = "Power Supply Trouble";
//    public static final String CONTROL_BAT_TROUBLE = "BAT Trouble";
//    public static final String CONTROL_ONLINE_OR_OFFLINE = "Online/Offline";

    public static final String CONTROL_CPU_FAIL = "CPU故障";
    public static final String CONTROL_NETWORK_TROUBLE = "网络故障";
    public static final String CONTROL_GROUND_FAULT = "接地故障";
    public static final String CONTROL_POWER_SUPPLY_TROUBLE = "电源故障";
    public static final String CONTROL_BAT_TROUBLE = "电池故障";
    public static final String CONTROL_ONLINE_OR_OFFLINE = "声音关";

    public static final int CONTROL_CPU_FAIL_INDEX = 5;
    public static final int CONTROL_NETWORK_TROUBLE_INDEX = 4;
    public static final int CONTROL_GROUND_FAULT_INDEX = 3;
    public static final int CONTROL_POWER_SUPPLY_TROUBLE_INDEX = 2;
    public static final int CONTROL_BAT_TROUBLE_INDEX = 1;
    public static final int CONTROL_ONLINE_OR_OFFLINE_INDEX = 0;

    public static  final Map<String, String> CONTROLONE_MAP = new HashMap<String, String>(){
        {
            put(CONTROL_CPU_FAIL_INDEX+"", CONTROL_CPU_FAIL);
            put(CONTROL_NETWORK_TROUBLE_INDEX+"", CONTROL_NETWORK_TROUBLE);
            put(CONTROL_GROUND_FAULT_INDEX+"", CONTROL_GROUND_FAULT);
            put(CONTROL_POWER_SUPPLY_TROUBLE_INDEX+"", CONTROL_POWER_SUPPLY_TROUBLE);
            put(CONTROL_BAT_TROUBLE_INDEX+"",CONTROL_BAT_TROUBLE);
            put(CONTROL_ONLINE_OR_OFFLINE_INDEX+"",CONTROL_ONLINE_OR_OFFLINE);
        }
    };

    public static  final Map<String, String> CONTROLTWO_MAP = new HashMap<String, String>(){
        {
            put(CONTROL_OPERATING_LEVLEL_1_INDEX+"",CONTROL_OPERATING_LEVEL_1);
            put(CONTROL_OPERATING_LEVLEL_2_INDEX+"", CONTROL_OPERATING_LEVEL_2);
            put(CONTROL_OPERATING_LEVLEL_3_INDEX+"", CONTROL_OPERATING_LEVEL_3);
            put(CONTROL_OPERATING_LEVLEL_4_INDEX+"", CONTROL_OPERATING_LEVEL_4);
            put(CONTROL_OPERATING_LEVLEL_5_INDEX+"", CONTROL_OPERATING_LEVEL_5);
            put(CONTROL_RESETING_INDEX+"", CONTROL_RESETING);
            put(CONTROL_PANEL_TEST_MODE_INDEX+"", CONTROL_PANEL_TEST_MODE);
            put(CONTROL_KEY_SWITCH_FREE_INDEX+"",CONTROL_KEY_SWITCH_FREE);
            put(CONTROL_COVER_CONTACT_OPEN_INDEX+"",CONTROL_COVER_CONTACT_OPEN);
        }
    };

    public static final Map<String, String> LOOP_MAP = new HashMap<String,String>(){
        {
            put(LOOP_RESETING_INDEX+"", LOOP_RESETING);
            put(LOOP_DISABLE_INDEX+"", LOOP_DISABLE);
            put(LOOP_TROUBLE_INDEX+"", LOOP_TROUBLE);
            put(LOOP_FIREALARM_INDEX+"", LOOP_FIREALARM);
        }
    };

}
