package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.entity.TaskAndForm;
import com.honeywell.fireiot.repository.TaskFormRepository;
import com.honeywell.fireiot.service.TaskFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @ClassName TaskFormServiceImpl
 * @Description 工作任务与相关巡检内容（task_and_form）,form 指formElement
 * @Author Monica Z
 * @Date 2019/1/17 09:48
 */
@Service
@Transactional
public class TaskFormServiceImpl  implements TaskFormService {
    @Autowired
    private TaskFormRepository tfRepository;

    /**
     * 根据任务Id，获取巡检内容formelement的id集合
     * @param taskId
     * @return
     */
    @Override
    public List<Long> getFormIds(long taskId) {

        return tfRepository.findFormIdByTaskId(taskId);
    }

    /**
     * 保存表单与工作任务关联
     * @param taskAndForm
     * @return
     */
    @Override
    public long save(TaskAndForm taskAndForm) {
        long id  = 0;
        if(taskAndForm.getId() == 0){
            id =  this.insert(taskAndForm);
        }else {
            id =  this.update(taskAndForm);
        }
        return id;
    }

    /**
     * 新增工作任务与巡检内容关联关系
     * @param taskAndForm
     * @return
     */
    @Override
    public long insert(TaskAndForm taskAndForm) {
        taskAndForm.setCreateTime(new Date());
        TaskAndForm newRecord =  tfRepository.save(taskAndForm);
        return newRecord.getId();
    }

    /**
     * 更新关联关系
     * @param taskAndForm
     * @return
     */
    @Override
    public long update(TaskAndForm taskAndForm) {
        taskAndForm.setUpdateTime(new Date());
        TaskAndForm newRecord = tfRepository.save(taskAndForm);
        return newRecord.getId();
    }

    /**
     * 删除工作任务与巡检内容关联关系
     * @param taskAndForm
     */
    @Override
    public void delete(TaskAndForm taskAndForm) {

        tfRepository.delete(taskAndForm);
    }

    @Override
    public void deleteByTaskIdAndDeviceId(long taskId, long deviceId) {
        tfRepository.deleteByTaskIdAndDeviceId(taskId,deviceId);
    }

    /**
     * 根据任务id,删除关联巡检内容或模版
     * @param taskId
     */
    @Override
    public void deleteByTaskId(long taskId) {

        tfRepository.deleteByTaskId(taskId);
    }

    @Override
    public void deleteByTaskIdAndTypeAndFormElementId(long taskId, int type, long formElementId) {
        tfRepository.deleteByTaskIdAndTypeAndFormElementId(taskId,type,formElementId);

    }

    @Override
    public void deleteByTaskIdAndDeviceIdAndFormElementId(long taskId, long deviceId,long formElementId ) {

        tfRepository.deleteByTaskIdAndDeviceIdAndFormElementId(taskId,deviceId,formElementId);
    }

    /**
     * 根据taskId与type查询devices
     * @param taskId
     * @param type
     * @return
     */
    @Override
    public List<Long> findByTaskIdAndType(long taskId, int type) {
        return tfRepository.findDeviceByTaskIdAndType(taskId, type);
    }

    @Override
    public List<Long> findFormByTaskIdAndDeviceId(long taskId, long deviceId) {
        return tfRepository.findByTaskIdAndDeviceId(taskId,deviceId);
    }

    @Override
    public List<Long> findElementByTaskIdAndType(long taskId, int type) {
        return tfRepository.findElementIdByTaskAndType(taskId, type);
    }


    @Override
    public List<TaskAndForm> findByTaskId(long taskId) {
        return tfRepository.findAllByTaskId(taskId);
    }
}
