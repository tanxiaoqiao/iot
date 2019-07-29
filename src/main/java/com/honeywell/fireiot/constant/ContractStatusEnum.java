package com.honeywell.fireiot.constant;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 4:32 PM 1/17/2019
 */
public enum ContractStatusEnum {
    NOTSTARTED(0, "未开始"),
    RUNNING(1,"执行中"),
    DUE(2,"已到期"),
    PASS(3, "验收通过"),
    NOPASS(4,"验收不通过"),
    TERMINATED(5,"已终止"),
    ARCHIVED(6,"已存档")
    ;

    private int code;
    private String msg;


    ContractStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }








}
