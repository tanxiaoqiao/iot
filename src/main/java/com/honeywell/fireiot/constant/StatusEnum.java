package com.honeywell.fireiot.constant;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
public class StatusEnum {

    //0已创建待派单 1已派单待接受 2已接受处理中 3已完成待验证 4已验证存档 5终止存档 6待审批 7已经审批 -1维保确认
    // public static final int CREATED = 0;

    public static final int unConfirm = -1;
    public static final int ARRANGE = 0;
    public static final int ACCEPT = 1;
    public static final int COMPLETE = 2;
    public static final int TRACE = 3;
    public static final int FINAL = 4;
    public static final int TERMINATE = 5;
    public static final int APPLAY_AUDIT = 6;

    public static final int AUDITED = 7;

    public static final int UPDATE = 10;
    public static final int REFUSE = 11;


}
