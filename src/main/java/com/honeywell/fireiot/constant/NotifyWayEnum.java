package com.honeywell.fireiot.constant;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 3:53 PM 1/17/2019
 */
public enum NotifyWayEnum {
    MESSAGE(0,"短信"),
    MAIL(1,"邮件"),
    INMAIL(2,"站内信"),
    APP(3, "APP 推送")
    ;

    private int code;

    private String msg;


    NotifyWayEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
