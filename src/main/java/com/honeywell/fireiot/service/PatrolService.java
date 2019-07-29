package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.PatrolCondition;
import com.honeywell.fireiot.dto.PatrolReport;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.InspectionResult;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.entity.Spot;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
public interface PatrolService {

    /**
     * detail
     *
     * @param patrolId
     * @return
     */
    List<Spot> findone(Long patrolId);


    /**
     * detail
     *
     * @return
     */
    List<InspectionResult> findSpotOne(Long patrolId, Long spotId);

    /**
     * find
     *
     * @param condition
     * @return
     */
    Pagination<Patrol> findByCondition(PatrolCondition condition);


    /**
     * update
     *
     * @param patrolId
     */
    void updateStatus(Long patrolId);

    List<InspectionResult> findDeviceOne(Long patrolId, Long spotId, Long deviceId);

    List<BusinessDevice> findSpotDevice(Long patrolId, Long spotId);

    PatrolReport report(String year);

    Boolean add(Patrol patrol);
}
