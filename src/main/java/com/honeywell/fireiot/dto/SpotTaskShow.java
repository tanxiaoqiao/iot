package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * @ClassName SpotTaskShow
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-01-23 13:56
 */
@Data
public class SpotTaskShow {

    private long spotId;
    /**
     * 点位安装位置
     */
    private String fullName;
    /**
     * 点位名称
     */
    private String name;
    /**
     * 工作任务
     */
    private String taskName;
    /**
     * 工作任务id
     */
    private long taskId;
    /**
     * 点位关联设备数量
     */
    private Integer deviceNum;
}
