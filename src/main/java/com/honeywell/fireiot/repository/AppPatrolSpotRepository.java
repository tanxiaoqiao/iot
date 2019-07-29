package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.AppPatrolSpot;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppPatrolSpotRepository
 * @author: dexter
 * @create: 2019-04-04 13:54
 * @description:
 **/
public interface AppPatrolSpotRepository extends MongoRepository<AppPatrolSpot, String> {
}
