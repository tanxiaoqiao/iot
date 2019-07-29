package com.honeywell.fireiot.constant;

/**
 * 日志类型
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/3/14 9:50 AM
 */
public enum TraceLogType {
    LOGIN("登录登出"),
    USER("人员管理"),
    DEVICE("设备操作");

    String description;

    TraceLogType(String des) {
        this.description = des;
    }

    public String getDescription() {
        return description;
    }
}
