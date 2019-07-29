package com.honeywell.fireiot.constant;



public enum ActionErrorEnum {

    SUCCESS(0, "success"),
    ACTIONNOTFOUND(10000,"Action is not found"),
    GATEWAYNOTEXIST(10001,"Gateway is not exist"),
    ONLINEFAILED(10010,"online failed"),
    DEVICENOTFOUND(10020,"device not found"),
    EVENTNOTEXIST(10021,"event not exist"),
    PARAMSERROE(10002,"params is error");




    private int code;

    private String msg;

    ActionErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
