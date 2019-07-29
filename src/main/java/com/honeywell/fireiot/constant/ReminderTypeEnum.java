package com.honeywell.fireiot.constant;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:36 PM 1/17/2019
 */
public enum ReminderTypeEnum {
    GROUP(0, "工作组"),
    STAFF(1, "员工"),
    ;
    private int code;

    private String msg;

    ReminderTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
