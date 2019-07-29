package com.honeywell.fireiot.dto;

import lombok.Data;

@Data
public class TaskScheduleModel {


    private String hour;

    private String minute;


    //0天 1周 2月
    private Integer jobType;

    private Integer[] dayOfWeeks;
    private Integer[] dayOfMonths;


    public TaskScheduleModel(String hour, String minute, Integer jobType, Integer[] dayOfWeeks, Integer[] dayOfMonths) {
        this.hour = hour;
        this.minute = minute;
        this.jobType = jobType;
        this.dayOfWeeks = dayOfWeeks;
        this.dayOfMonths = dayOfMonths;

    }

    public TaskScheduleModel() {

    }
}
