package com.honeywell.fireiot.water.constant;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author: xiaomingCao
 * @create: 1/20/2019
 */
@Getter
public enum WaterDeviceStatusEnum {

    OFFLINE(2, "离线"),
    ONLINE(0, "正常"),
    EXCEPTION(1, "异常");

    private int value;

    private String description;

    WaterDeviceStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static WaterDeviceStatusEnum get(int value){
        return Stream.of(WaterDeviceStatusEnum.class.getEnumConstants())
                .filter(en -> en.getValue() == value)
                .findFirst()
                .get();
    }
}
