package com.honeywell.fireiot.entity;

import lombok.Data;

/**
 * 环境信息
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/4/11 11:51 AM
 */
@Data
public class Env {
    /**
     * sso绑定的系统类型枚举
     * 1 - 消防水系统
     * 5 - 消防火系统
     * 8 - 点检系统
     * 9 - 维保系统
     */
    private int resource = 1;

    /**
     * 系统类型，为字符串
     * water
     * fire
     * polling
     * maintenance
     */
    private String systemType = "water";

    /**
     * SSO的登录凭证
     */
    private String ssoToken;

    public void setResource(int resource) {
        this.resource = resource;
        switch (resource) {
            case 1:
                this.systemType = "water";
                break;
            case 5:
                this.systemType = "fire";
                break;
            case 8:
                this.systemType = "polling";
                break;
            case 9:
                this.systemType = "maintenance";
                break;
        }
    }
}
