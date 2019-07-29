package com.honeywell.fireiot.constant;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 4:26 PM 1/17/2019
 */
public enum PayTypeEunm {
    RECEIPT(0, "收款"),
    PAYMENT(1,"付款"),
    ;
    private int code;
    private String msg;

    PayTypeEunm(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
