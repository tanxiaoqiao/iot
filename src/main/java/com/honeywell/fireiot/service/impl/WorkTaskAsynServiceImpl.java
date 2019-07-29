package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.service.TaskDeviceService;
import com.honeywell.fireiot.service.TaskFormService;
import com.honeywell.fireiot.service.WorkTaskAsynService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @ClassName WorkTaskAsynServiceImpl
 * @Description 异步处理工作任务相关的操作
 *   比如 删除更新所关联的巡检内容与设备
 * @Author Monica Z
 * @Date 2019/1/17 15:22
 */
@Service
public class WorkTaskAsynServiceImpl implements WorkTaskAsynService {
    @Autowired
    private TaskFormService taskFormService;
    @Autowired
    private TaskDeviceService taskDeviceService;
    /**
     * 异步操作
     * 删除与工作相关的关联
     *
     *
     * 查询与工作任务相关的巡检内容，删除关联关系
     *
     * @param taskId
     */
    @Async
    @Override
    public void deleteRelationInspect(long taskId) {
       taskFormService.deleteByTaskId(taskId);
       taskDeviceService.deleteByTaskId(taskId);
    }
}
