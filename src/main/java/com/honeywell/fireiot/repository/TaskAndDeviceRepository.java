package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.TaskAndDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @InterfaceName TaskAndDeviceRepository
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/17 15:36
 */
@Repository
public interface TaskAndDeviceRepository  extends JpaRepository<TaskAndDevice,Long>, JpaSpecificationExecutor<TaskAndDevice> {
    List<Long> findDeviceIdByTaskId(long taskId);

    void deleteByTaskId(long taskId);
    @Query(value ="delete from TaskAndDevice td where td.taskId =:taskId and td.deviceId in :deviceIds ")
    void deleteByTaskIdAndAndDeviceIdIn(@Param("taskId") long taskId,@Param("deviceIds") List<Long> deviceIds);

    @Modifying
    @Query(value = "delete from TaskAndDevice td where td.taskId =:taskId and td.deviceId =:deviceId")
    void deleteByTaskIdAndAndDeviceId(@Param("taskId") long taskId,@Param("deviceId") long deviceId);

    @Query(value = "select td from TaskAndDevice td where td.taskId =:taskId",countQuery = "select count(td) from  TaskAndDevice td where td.taskId =:taskId")
    Page<TaskAndDevice> findAllByTaskId(@Param("taskId")long taskId, Pageable pageable);
}
