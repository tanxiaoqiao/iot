package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: Kris
 * @Date: 2019-07-12  13:52
 */
@Data
public class PatrolReport {
    Integer total;
    Integer spotNums;
    Integer taskCount;
    List<MonthPatrol> monthPatrols;
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

    public PatrolReport() {
    }

    public PatrolReport(Integer total, Integer spotNums, Integer taskCount, List<MonthPatrol> monthPatrols, Integer normalCount, Integer delayCount, Integer unfinishCount, Integer normalNums, Integer exceptionNums, Integer repairNums, Integer missNums, Integer supplementNums) {
        this.total = total;
        this.spotNums = spotNums;
        this.taskCount = taskCount;
        this.monthPatrols = monthPatrols;
        this.normalCount = normalCount;
        this.delayCount = delayCount;
        this.unfinishCount = unfinishCount;
        this.normalNums = normalNums;
        this.exceptionNums = exceptionNums;
        this.repairNums = repairNums;
        this.missNums = missNums;
        this.supplementNums = supplementNums;
    }
}
