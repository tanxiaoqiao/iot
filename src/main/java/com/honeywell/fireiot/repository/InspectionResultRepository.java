package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.InspectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @InterfaceName InspectionResultRepository
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/21 10:18
 */
@Repository
public interface InspectionResultRepository extends JpaRepository<InspectionResult, Long>, JpaSpecificationExecutor<InspectionResult> {


    @Query(value = " select ir from InspectionResult ir where ir.deviceId = ?1 and ir.spotId=?2 and ir.patrolId= ?3")
    List<InspectionResult> findBySpotIdAndDeviceId(Long deviceId, Long spotId, Long patrolId);


    /**
     * 综合查询
     * @param ids
     * @return
     */
    @Query(value = " select ir from InspectionResult ir where ir.type = 0 and ir.id in (:ids) ")
    List<InspectionResult> findCommonByIds(@Param("ids") List<Long> ids);

    /**
     * 设备查询
     * @param ids
     * @return
     */

    @Query(value = "SELECT ir.deviceId From  InspectionResult ir where ir.type = 1 and ir.id in (:ids) group by ir.deviceId")
    List<Long> findDeviceByIds(@Param("ids") List<Long> ids);
}
