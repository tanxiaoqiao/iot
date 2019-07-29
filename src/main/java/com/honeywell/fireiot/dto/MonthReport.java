package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * @Author: Kris
 * @Date: 2019-07-11  12:28
 */
@Data
public class MonthReport {
    private Integer month;
    private Integer total;
    //正常完成个数
    Integer normalCount;
    //终止完成个数
    Integer terminalCount;
    //未完成个数
    Integer unfinishCount;
}
