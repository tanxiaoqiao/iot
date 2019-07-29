package com.honeywell.fireiot.service;


import com.honeywell.fireiot.entity.TaskAndForm;

import java.util.List;

/**
 * @InterfaceName TaskFormService
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/17 09:45
 */
public interface TaskFormService {

    List<Long>  getFormIds(long taskId);

    long save(TaskAndForm taskAndForm);
    long insert(TaskAndForm taskAndForm);
    long update(TaskAndForm taskAndForm);
    void delete(TaskAndForm taskAndForm);
    void deleteByTaskIdAndDeviceId(long taskId,long deviceId);
    void deleteByTaskId(long taskId);
    void deleteByTaskIdAndTypeAndFormElementId(long taskId,int type,long formElementId);
    void deleteByTaskIdAndDeviceIdAndFormElementId(long taskId,long deviceId,long formElementId);
    List<Long> findByTaskIdAndType(long taskId, int type);
    List<Long> findFormByTaskIdAndDeviceId(long taskId, long deviceId);
    List<Long>findElementByTaskIdAndType(long taskId, int type);

    List<TaskAndForm> findByTaskId(long taskId);

}
