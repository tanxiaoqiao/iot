package com.honeywell.fireiot.water.schedule.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xiaomingCao
 * @date: 2018/12/28
 */
@Data
@ConfigurationProperties("water.lora.api.query")
public class LoraApiQueryProperties {

    private int processor = 4;
    /**
     * 执行调用api延迟时间，每次掉用完后会sleep相应的毫秒数
     */
    private int executeDelay = 7000;

    /**
     * 执行调用api线程数量的最大值
     */
    private int executeThread = 8;

    /**
     * schedule执行间隔时间，任务的触发时间间隔
     */
    private int interval = 5*60*100;

    private String address = "http://10.78.115.175:8000/api";

    /**
     * 需要主动轮询的设备
     *
     * example: WATER_SYS_FLOWMETER@FE4451530D0A:220,WATER_SYS_FOO@FE1234:20
     *
     * WATER_SYS_FLOWMETER@FE4451530D0A:220为主动轮询设备类型为WATER_SYS_FLOWMETER
     * 的设备，并主动发送消息中Data为FE4451530D0A，FPort为220
     * 多个用逗号分隔
     */
    private String tasks = "";

    private String username = "admin";

    private String password = "admin";

    private String loginURI = "/v1/user/login";

    private String queryURI = "/v1/nodes/{eui}/queue";

    private String gatewayStatusURI = "/v1/gateway/{eui}";
}
