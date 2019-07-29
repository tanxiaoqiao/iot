package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.SystemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:54 AM
 */
@Repository
public interface SystemTypeRepository extends JpaRepository<SystemType, Long>, JpaSpecificationExecutor<SystemType> {
    String FIRESYSTEM = "报警系统";
    String WATERSYSTEM = "水系统";
    List<SystemType> findByName(String name);

    List<SystemType> findByParentSystemType(SystemType parentSystemType);

}
