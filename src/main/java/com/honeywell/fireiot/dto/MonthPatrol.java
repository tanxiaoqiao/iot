package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * @Author: Kris
 * @Date: 2019-07-12  13:55
 */
@Data
public class MonthPatrol {
    Integer month;
    Integer total;
    Integer spotNums;
    //正常完成个数
    Integer normalCount;
    //延期完成个数
    Integer delayCount;
    //未完成个数
    Integer unfinishCount;
    //正常个数
    Integer normalNums ;
    //异常个数
    Integer exceptionNums ;
    //报修个数
    Integer repairNums ;
    //漏检个数
    Integer missNums ;
    //补检个数
    Integer supplementNums ;


    public MonthPatrol(Integer month, Integer total, Integer normalCount, Integer delayCount, Integer unfinishCount, Integer spotNums, Integer normalNums, Integer exceptionNums, Integer repairNums, Integer missNums, Integer supplementNums) {
        this.month = month;
        this.total = total;
        this.normalCount = normalCount;
        this.delayCount = delayCount;
        this.unfinishCount = unfinishCount;
        this.spotNums=spotNums;
        this.normalNums = normalNums;
        this.exceptionNums = exceptionNums;
        this.repairNums = repairNums;
        this.missNums = missNums;
        this.supplementNums = supplementNums;
    }

    public MonthPatrol() {
    }
}
