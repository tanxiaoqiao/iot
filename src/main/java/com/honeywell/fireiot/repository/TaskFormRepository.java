package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.TaskAndForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @InterfaceName TaskFormRepository
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/17 09:49
 */
@Repository
public interface TaskFormRepository extends JpaRepository<TaskAndForm,Long>, JpaSpecificationExecutor<TaskAndForm> {

    @Query(value = "select tf.formElementId from TaskAndForm tf where tf.taskId = ?1")
    List<Long>  findFormIdByTaskId(long taskId);

    @Transactional(rollbackOn = Exception.class)
    @Modifying
    @Query(value = "delete from wo_task_form where task_id =?1 ",nativeQuery = true)
    void deleteByTaskId(long taskId);

    @Modifying
    @Query(value = "delete from TaskAndForm  tf where tf.taskId =:taskId and tf.deviceId =:deviceId")
    void deleteByTaskIdAndDeviceId(@Param("taskId") long taskId,@Param("deviceId") long deviceId);

    @Modifying
    @Query(value ="delete from TaskAndForm  tf where tf.taskId =:taskId  and tf.deviceId =:deviceId and tf.formElementId in :elementIds")
    void deleteByTaskIdAndDeviceIdAndFormElementIdIn(@Param("taskId") long taskId,@Param("deviceId") long deviceId,@Param("elementIds") List<Long> elementIds);
    /**
     * 根据taskid 与type 获取设备id集合
     * @param taskId
     * @param type
     * @return
     */
    @Query(value = "select tf.deviceId from TaskAndForm  tf where tf.taskId = (:taskId) and tf.type =:type")
    List<Long> findDeviceByTaskIdAndType(@Param("taskId") long taskId, @Param("type") int type);

    /**
     * 根据taskid 与设备id 获取element集合
     * @param taskId
     * @param deviceId
     * @return
     */
    @Query(value = "select  tf.formElementId from TaskAndForm tf where tf.taskId =?1 and tf.deviceId =?2")
    List<Long> findByTaskIdAndDeviceId(long taskId, long deviceId);

    /**
     * 根据taskID与type 获取element集合
     * @param taskId
     * @param type
     * @return
     */
    @Query(value = "select tf.formElementId from TaskAndForm  tf where tf.taskId =?1 and tf.type =?2")
    List<Long> findElementIdByTaskAndType(@Param("taskId") long taskId, @Param("type") int type);

    @Modifying
    @Query(value ="delete from TaskAndForm tf where tf.taskId =:taskId and tf.type =:type and tf.formElementId =:formElementId")
    void deleteByTaskIdAndTypeAndFormElementId(@Param("taskId")long taskId,@Param("type") int type,@Param("formElementId") long formElementId);

    @Modifying
    @Query( value="delete from TaskAndForm tf where tf.taskId =:taskId and tf.deviceId =:deviceId and tf.formElementId =:formElementId")
    void  deleteByTaskIdAndDeviceIdAndFormElementId(@Param("taskId")long taskId,@Param("deviceId")long deviceId,@Param("formElementId")long formElementId);

    List<TaskAndForm> findAllByTaskId(long taskId);
}
