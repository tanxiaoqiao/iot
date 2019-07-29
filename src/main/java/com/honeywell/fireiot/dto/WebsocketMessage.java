package com.honeywell.fireiot.dto;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 2:57 PM 6/21/2018
 */
public class WebsocketMessage {
    // 1首页  2即时消息 3工单推送
    private Integer type ;
    private Object data;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "WebsocketMessage{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
