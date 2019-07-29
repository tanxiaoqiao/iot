package com.honeywell.fireiot.service;

import com.honeywell.fireiot.entity.AppPatrolSpot;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppPatrolSpotService
 * @author: dexter
 * @create: 2019-04-04 13:57
 * @description:
 **/
public interface AppPatrolSpotService {

    /**
     * 保存到mongo
     *
     * @param appPatrolSpot
     * @return
     */
    String save(AppPatrolSpot appPatrolSpot);

    /**
     * 异步存储到相应实体
     *
     * @param appPatrolSpot
     * @return
     */
    void analyze(AppPatrolSpot appPatrolSpot);
}
