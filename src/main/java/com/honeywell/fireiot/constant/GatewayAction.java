package com.honeywell.fireiot.constant;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 6:05 PM 6/5/2018
 */
public class GatewayAction {
    //    网关上线
    public static final String ONLINE = "online";
    // 遗言机制
    public static final String WILL = "will";
    //    上报事件
    public static final String ALARM = "alarm";
    //    获取网关信息ack
    public static final String GETGATEWAYINFOACK = "getgatewayinfo-ack";
    //    上传网关消息
    public static final String SENDGATEWAYINFO = "sendgatewayinfo";
    //    网关升级ack
    public static final String UPGRADEGATEWAYACK = "upgradegateway-ack";
    //校时
    public static final String GETTIME = "gettime";
}
