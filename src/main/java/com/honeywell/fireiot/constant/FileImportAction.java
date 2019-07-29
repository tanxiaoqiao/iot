package com.honeywell.fireiot.constant;


import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:10 PM 10/18/2018
 */
public class FileImportAction {
    //错误情况
    public static final int ERR_SUCCESS = 0;
    public static final int ERR_FILE = -1; //文件错误
    public static final int ERR_CLUMN = -2;//列错误
    public static final int ERR_REPEAT = -3;//存在重复
    public static final int ERR_OPEARA_NOTEXIST = -4;//操作不存在



    //导入或者删除操作
    public static  final int REMOVE_DBDATA = 0;//删除数据
    public static  final int ADD_DBDATA    = 1;//增加数据
    public static final int  GENERATE_JSON = 3;//生成json数据


    //excel 成功或者失败情况
    public static final String SUCCESSROWS = "sucessrow_num";
    public static final String FAILROWS = "failrow_num";

    //文件后缀名
    public static final String SUFFIX_XLS = ".xls";
    public static final String SUFFIX_XLSX = ".xlsx";
    public static final String SUFFIX_CSV = ".csv";


    public static final int TITLE_ROWINDEX = 1;

    public static final int IGONREROWS_BASE = 3;
    public static final int CLUMNNUM_BASE  = 16;

    public static final int IGONREROWS_ALLDEVICE=5;
    public static final int CLUMNNUM_ALLDEVICE=32;

    public static final int IGONREROWS_SINGLET=3;
    public static final int CLUMNNUM__SINGLET=40;


    //消防系统
    public static final int  FIREDEVICE_SHEETINDEX = 1;//脚本页
    public static final int FIREDEVICE_POLYM_SHEETINDEX = 2;//多态设备
    public static final int FIREDEVICE_CLUMNNUM = 32;//行数
    public static  final int FIREDEVICE_TITLE_ROWINDEX = 0;//标题行

    public static final int FIREDEVICE_SINGLETON_DATA = 1;//单态设备
    public static final int FIREDEVICE_POLYM_DATA  = 2;//多态设备

    public static final int FIREDEVICE_COLUMN_NETWORK = 1; //网络号
    public static final int FIREDEVICE_COLUMN_BUILDING = 2;//楼栋
    public static final int FIREDEVICE_COLUMN_FLOOR = 3;//楼层
    public static final int FIREDEVICE_COLUMN_ZONETYPE = 4;//区类型
    public static final int FIREDEVICE_COLUMN_ZONE = 5;//区
    public static final int FIREDEVICE_COLUMN_POINT = 6;//点
    public static final int FIREDEVICE_COLUMN_TAG = 8;//设备地址
    public static final int FIREDEVICE_COLUMN_ERRORFLAG=10;
    public static final int FIREDEVICE_COLUMN_LOOP = 11;//回路号
    public static final int FIREDEVICE_COLUMN_DEVICENO=12;//设备编号
    public static final int FIREDEVICE_COLUMN_RESIGTERNO = 15;//寄存器号


    public static final int FIREDEVICE_COLUMN_GATEWAY = 31;//网关号
    public static final int FIREDEVICE_COLUMN_B0 = 31;
    public static final int FIREDEVICE_COLUMN_B15 = 16;

    public static final String  FIRE_SYSTEM_NAME = "消防报警系统";

    public static final String  JSON_KEY = "devicePoints";//生成Json 文件的key
    public static final String  JSON_DIR = "./device/";




    public static final String BASEINFOSHEET = "总表";

    public static final String ALLDEVICESHEET = "原始表";

    public static final String GATEWAY="网关";

    public static final List<String>  SINGLET_STATE_TYPE_NAMES= new ArrayList<String>(){
        {
            add("烟感");
            add("感温");
            add("传统感烟");
            add("手报");
            add("消火栓");
            add("模块");
            add("声光");
            add("警铃");
            add("防爆手报");
            add("防爆烟感");
            add("火焰探测器");
            add("可燃气");


        }

    };
    public static final int SHEET_TYPE_BASE=1;

    public static final int SHEET_TYPE_ALLDEVICE=2;

    public static final int SHEET_TYPE_SINGLET=3;

    public static final int SHEET_TYPE_POLYM=4;

    public static final int SHEET_TYPE_GATEWAY=5;

    //消防系统新模板列索引
    public static final int DEVICE_COLUMN_ID=0;//台账ID

    public static final int DEVICE_COLUMN_SYSTEMTYPE=3;//系统类型

    public static final int DEVICE_COLUMN_DEVICETYPE=4;//设备类型

    public static final int DEVICE_COLUMN_TOPAREA=5;//园区

    public static final int DEVICE_COLUMN_AREA=6;//区域

    public static final int DEVICE_COLUMN_BUILDING=7;//楼栋

    public static final int DEVICE_COLUMN_FLOOR=8;//层

    public static final int DEVICE_COLUMN_DEVICELABEL=9;//设备标签

    public static final int DEVICE_COLUMN_DEVICENO=11;//设备编号

    public static final int DEVICE_COLUMN_BRANDNAME=12;//品牌

    public static final String UPLOAD_BUILDING="C06,C07,C41,C42";

    public static final Long MT_GATEWAY_ID=3L;










}
