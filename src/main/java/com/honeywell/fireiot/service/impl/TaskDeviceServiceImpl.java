package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.dto.BusinessDeviceDto;
import com.honeywell.fireiot.dto.TaskDeviceDto;
import com.honeywell.fireiot.entity.TaskAndDevice;
import com.honeywell.fireiot.entity.TaskAndForm;
import com.honeywell.fireiot.repository.TaskAndDeviceRepository;
import com.honeywell.fireiot.repository.TaskFormRepository;
import com.honeywell.fireiot.service.TaskDeviceService;
import com.honeywell.fireiot.service.TaskFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @ClassName TaskDeviceServiceImpl
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/17 15:25
 */
@Service
public class TaskDeviceServiceImpl implements TaskDeviceService {
    @Autowired
    private TaskAndDeviceRepository taskAndDeviceRepository;
    @Autowired
    private TaskFormRepository taskFormRepository;
    @Autowired
    private TaskFormService taskFormService;
    /**
     * 判断工作任务是够有关联设备
     * @param taskId
     * @return
     */
    @Override
    public Boolean isExist(long taskId) {
        List<Long> deviceIds = getDeviceIdByTaskId(taskId);
        if (deviceIds.size()> 0){
            return true;
        } else {
            return  false;

        }
    }

    /**
     * 获取工作任务关联的device ID
     * @param taskId
     * @return
     */
    @Override
    public List<Long> getDeviceIdByTaskId(long taskId) {

        List<Long> deviceIds = taskAndDeviceRepository.findDeviceIdByTaskId(taskId);
        return deviceIds;
    }

    /**
     * 保存工作任务与设备的关联关系
     * @param taskDeviceDto
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public int  save(TaskDeviceDto taskDeviceDto) {
        int count = 0;

        List<BusinessDeviceDto> deviceDtos = taskDeviceDto.getDevices();
        if(deviceDtos.size() > 0){
            for(int i=0; i< deviceDtos.size(); i++){
                TaskAndDevice taskAndDevice = new TaskAndDevice();

                // locationId 空间id
                taskAndDevice.setLocationId(deviceDtos.get(i).getLocationId());
                // 空间名称
                taskAndDevice.setLocationName(deviceDtos.get(i).getLocationDetail());
                // deviceLabel 设备名称
                taskAndDevice.setDeviceLabel(deviceDtos.get(i).getDeviceLabel());
                // deviceNo 设备编码
                taskAndDevice.setDeviceNo(deviceDtos.get(i).getDeviceNo());
                // deviceId 设备id
                taskAndDevice.setDeviceId(deviceDtos.get(i).getId());
                // systemName 系统名称
                taskAndDevice.setSystemName(deviceDtos.get(i).getSystemType().getName());
                // taskId 任务id
                taskAndDevice.setTaskId(taskDeviceDto.getTaskId());
                // 新增设备与任务关联
                this.insert(taskAndDevice);
                count ++;
            }
        }
        return count;

//        List<Long> deviceIds = taskDeviceDto.getDeviceId();
//        for (int i = 0; i< deviceIds.size(); i++){
//            TaskAndDevice taskAndDevice = new TaskAndDevice();
//            if (taskDeviceDto.getId() == 0){
//                taskAndDevice.setDeviceId(deviceIds.get(i));
//                taskAndDevice.setTaskId(taskDeviceDto.getTaskId());
//                this.insert(taskAndDevice);
//            }else {
//                taskAndDevice.setDeviceId(deviceIds.get(i));
//                taskAndDevice.setTaskId(taskDeviceDto.getTaskId());
//                this.update(taskAndDevice);
//            }
//        }
    }

    /**
     * 新增工作任务与设备关联
     * @param taskAndDevice
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public long insert(TaskAndDevice taskAndDevice) {
        taskAndDevice.setCreateTime(new Date());
        TaskAndDevice newRecord = taskAndDeviceRepository.save(taskAndDevice);
        return newRecord.getId();
    }

    /**
     * 更新工作任务与设备关联
     * @param taskAndDevice
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public long update(TaskAndDevice taskAndDevice) {
        taskAndDevice.setUpdateTime(new Date());
        TaskAndDevice newRecord = taskAndDeviceRepository.save(taskAndDevice);
        return newRecord.getId();
    }

    /**
     * 批量删除工作任务所关联的设备
     * @param taskDeviceDto
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void batchDelete(TaskDeviceDto taskDeviceDto) {
        List<Long> deviceIds = taskDeviceDto.getDeviceId();
        if(deviceIds.size()> 0){
            for (int i = 0; i < deviceIds.size(); i++){
                // 删除工作任务与设备关联
//                TaskAndDevice taskAndDevice = new TaskAndDevice();
//                taskAndDevice.setTaskId(taskDeviceDto.getTaskId());
//                taskAndDevice.setDeviceId(deviceIds.get(i));
//                this.delete(taskAndDevice);
                this.deleteByTaskIdAndDeviceId(taskDeviceDto.getTaskId(),deviceIds.get(i));
                // 删除设备相关的巡检内容

               List<TaskAndForm> list= taskFormService.findByTaskId(taskDeviceDto.getTaskId());
               if(list.size() > 0){
//
                   taskFormRepository.deleteByTaskIdAndDeviceId(taskDeviceDto.getTaskId(),deviceIds.get(i));
//
               }

            }
        }
    }

    @Override
    public void deleteByTaskIdAndDeviceId(long taskId, long deviceId) {
      taskAndDeviceRepository.deleteByTaskIdAndAndDeviceId(taskId,deviceId);
    }

    /**
     * 删除关联关系
     * @param taskAndDevice
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void delete(TaskAndDevice taskAndDevice) {
        taskAndDeviceRepository.delete(taskAndDevice);
    }

    @Override
    public void deleteByTaskId(long taskId) {
        taskAndDeviceRepository.deleteByTaskId(taskId);
    }

    /**
     * 工作任务关联设备的个数
     * @param taskId
     * @return
     */
    @Override
    public int relatedDeviceNum(long taskId) {
        List<Long> deviceIds = getDeviceIdByTaskId(taskId);
        int n = deviceIds.size();
        return n;
    }

    @Override
    public List<Long> getFormIdByTaskIdAndDeviceId(long taskId, long deviceId) {
        return null;
    }

    @Override
    public Page<TaskAndDevice> findAllByTaskId(long taskId,Pageable pageable) {
        return taskAndDeviceRepository.findAllByTaskId(taskId,pageable);
    }
}
