package com.honeywell.fireiot.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppInitInfoVersion
 * @author: dexter
 * @create: 2019-04-01 16:21
 * @description: 每日定时任务生成Spot以及Device的相应version存储于Mongo
 **/
@Data
@Document(collection = "appInitInfoVersion")
public class AppInitInfoVersion {

    @Id
    private String id;

    /**
     * 点位版本号
     * 以"spot_yyyy_MM_dd"方式存储
     *
     */
    private String spotVersion;

    /**
     * 设备版本号
     * 以"device_yyyy_MM_dd"方式存储
     */
    private String deviceVersion;

    private String createDate;
}
