package com.honeywell.fireiot.service;



import com.honeywell.fireiot.dto.TaskDeviceDto;
import com.honeywell.fireiot.entity.TaskAndDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @InterfaceName TaskDeviceService
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/17 15:25
 */
public interface TaskDeviceService {
    Boolean isExist(long taskId);
    List<Long>  getDeviceIdByTaskId(long taskId);
    int save(TaskDeviceDto taskDeviceDto);
    long insert(TaskAndDevice taskAndDevice);
    long update(TaskAndDevice taskAndDevice);
    void batchDelete(TaskDeviceDto taskDeviceDto);
    void deleteByTaskIdAndDeviceId(long taskId,long deviceId);
    void delete(TaskAndDevice taskAndDevice);
    void deleteByTaskId(long taskId);
    int relatedDeviceNum(long taskId);
    List<Long> getFormIdByTaskIdAndDeviceId(long taskId, long deviceId);
    Page<TaskAndDevice> findAllByTaskId(long taskId,Pageable pageable);



}
