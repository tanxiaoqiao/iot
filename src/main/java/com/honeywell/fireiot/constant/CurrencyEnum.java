package com.honeywell.fireiot.constant;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 4:14 PM 1/17/2019
 */
public enum CurrencyEnum {
    RMB(0,"人民币"),
    DOLLAR(1,"美元")
    ;

    private int code;
    private String msg;

    CurrencyEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
