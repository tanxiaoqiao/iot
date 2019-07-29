package com.honeywell.fireiot.bo;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 10:16 PM 1/7/2019
 */
@Data
public class WebSocketMessage {
    private int messageType;
    private Object data;

}

