package com.honeywell.fireiot.constant;

public enum StatusType {

    NORMAL(0),
    FORBIDEN(1),
    DELETE(2);

    private int value;

    private StatusType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
