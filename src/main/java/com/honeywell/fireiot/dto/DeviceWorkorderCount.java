package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * @Author: Kris
 * @Date: 2019-07-09  12:09
 */
@Data
public class DeviceWorkorderCount {

    private Long arrange;
    private Long accept;
    private Long complete;
    private Long trace;
    private Long end;
    private Long terminated;
    private Long total;

    public DeviceWorkorderCount() {
    }

    public DeviceWorkorderCount(Long arrange, Long accept, Long complete, Long trace, Long end, Long terminated, Long total) {
        this.arrange = arrange;
        this.accept = accept;
        this.complete = complete;
        this.trace = trace;
        this.end = end;
        this.terminated = terminated;
        this.total = total;
    }
}
