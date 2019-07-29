package com.honeywell.fireiot.constant;

/**
 * 数据库操作符类型
 *
 * @Author: zhenzhong.wang
 * @Date: 8/27/2018 9:58 AM
 */
public enum DBOperator {

    AND(0),
    OR(1);

    private int value;

    private DBOperator(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
