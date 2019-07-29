package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.AppInitInfoVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppInitInfoVersionRepository
 * @author: dexter
 * @create: 2019-04-01 16:33
 * @description:
 **/
@Repository
public interface AppInitInfoVersionRepository extends MongoRepository<AppInitInfoVersion, String> {
}
