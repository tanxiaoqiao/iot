package com.honeywell.fireiot.dto;

import lombok.Data;

import java.time.Month;
import java.util.List;

/**
 * @Author: Kris
 * @Date: 2019-07-10  11:00
 */
@Data
public class WorkorderReport {
    //年度总工单数
    Integer yearCount;
    //年度正常完成个数
    Integer normalCount ;
    //年度终止完成个数
    Integer terminalCount ;
    //年度未完成个数
    Integer unfinishCount;
    //每月工单统计;
    List<MonthReport>  monthReport;

    public WorkorderReport() {
    }

    public WorkorderReport(Integer yearCount, Integer normalCount, Integer terminalCount, Integer unfinishCount, List<MonthReport> monthReport) {
        this.yearCount = yearCount;
        this.normalCount = normalCount;
        this.terminalCount = terminalCount;
        this.unfinishCount = unfinishCount;
        this.monthReport = monthReport;
    }
}
