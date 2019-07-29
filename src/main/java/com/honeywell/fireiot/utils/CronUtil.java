package com.honeywell.fireiot.utils;

import com.honeywell.fireiot.dto.TaskScheduleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CronUtil.class);


    public static String createCronExpression(TaskScheduleModel taskScheduleModel) {
        //秒
        StringBuffer cronExp = new StringBuffer("00 ");

        if (null != taskScheduleModel.getMinute()
                && null != taskScheduleModel.getHour()) {
            //分
            cronExp.append(taskScheduleModel.getMinute()).append(" ");
            //小时
            cronExp.append(taskScheduleModel.getHour()).append(" ");
            //每天
            if (taskScheduleModel.getJobType().intValue() == 0) {
                cronExp.append("* * ?");
            }

            //按每周
            else if (taskScheduleModel.getJobType().intValue() == 1) {
                //一个月中第几天
                cronExp.append("? * ");
                Integer[] weeks = taskScheduleModel.getDayOfWeeks();
                for (int i = 0; i < weeks.length; i++) {
                    if (i == 0) {
                        cronExp.append(weeks[i]);
                    } else {
                        cronExp.append(",").append(weeks[i]);
                    }
                }

            }

            //按每月
            else if (taskScheduleModel.getJobType().intValue() == 2) {
                //一个月中的哪几天
                Integer[] days = taskScheduleModel.getDayOfMonths();
                for (int i = 0; i < days.length; i++) {
                    if (i == 0) {
                        cronExp.append(days[i]);
                    } else {
                        cronExp.append(",").append(days[i]);
                    }
                }
                //月份
                cronExp.append(" * ?");
            }
        } else {
            //时或分或秒参数未配置
            LOGGER.info("not_config_taskScheduleModel");
        }
        return cronExp.toString();
    }

    public static String createIntervalExpression(TaskScheduleModel taskScheduleModel, Integer times, Integer day, Integer month) {
        //秒
        StringBuffer cronExp = new StringBuffer("0 ");

        if (null != taskScheduleModel.getMinute()
                && null != taskScheduleModel.getHour()) {
            //分
            cronExp.append(taskScheduleModel.getMinute()).append(" ");
            //小时
            cronExp.append(taskScheduleModel.getHour()).append(" ");
            //每隔几月
            cronExp.append(day).append(" ").append(month).append("/").append(times).append(" ?");

        } else {
            //时或分或秒参数未配置
            LOGGER.info("not_config_taskScheduleModel");
        }
        return cronExp.toString();
    }

}
