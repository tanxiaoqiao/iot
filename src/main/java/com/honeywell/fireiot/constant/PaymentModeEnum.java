package com.honeywell.fireiot.constant;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 4:14 PM 1/17/2019
 */
public enum PaymentModeEnum {
    PAYMENT(0,"支付"),
    CASH(1,"现金"),
    DRAFT(2,"汇票"),
    ;
    private int code;
    private String msg;

    PaymentModeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
