package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.DailyMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Repository
public interface DailyMaintenanceReporsitory extends JpaRepository<DailyMaintenance, Long>, JpaSpecificationExecutor<DailyMaintenance> {

    @Transactional(rollbackOn = Exception.class)
    @Query("delete from DailyMaintenance dm where  dm.maintenanceId=:maintenanceId and dm.startTime>:startTime")
    @Modifying
    void deleteByMaintenanceId(@Param("maintenanceId") Long maintenanceId, @Param("startTime") Date startTime);


    @Query("from DailyMaintenance dm where dm.isAuto = true and dm.startTime>:startTime and dm.startTime<:endTime")
    List<DailyMaintenance> findAllByStartTime(@Param("startTime") Date startTime,@Param("endTime") Date endTime);


    @Query("from DailyMaintenance dm where dm.isAuto = true and dm.startTime>:startTime and dm.startTime<:endTime")
    List<DailyMaintenance> findBetweenStartTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
