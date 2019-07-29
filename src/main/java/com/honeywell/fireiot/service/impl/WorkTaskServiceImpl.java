package com.honeywell.fireiot.service.impl;



import com.honeywell.fireiot.constant.PollingType;
import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.repository.SpotRepository;
import com.honeywell.fireiot.repository.TaskFormRepository;
import com.honeywell.fireiot.repository.WorkTaskRepository;
import com.honeywell.fireiot.service.*;
import com.honeywell.fireiot.utils.JpaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class WorkTaskServiceImpl implements WorkTaskService  {
    @Autowired
    private SpotAndTaskService stService;
    @Autowired
    private WorkTaskRepository wtRepository;
    @Autowired
    private TemplateService templateService;

    @Autowired
    private TaskFormService taskFormService;
    @Autowired
    private WorkTaskAsynService workTaskAsynService;
    @Autowired
    private InspectContentService inspectContentService;
    @Autowired
    private TemplateAndElementService templateAndElementService;
    @Autowired
    private BusinessDeviceService businessDeviceService;
    @Autowired
    private TaskDeviceService taskDeviceService;

    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private TaskFormRepository taskFormRepository;

    private static Logger logger = LoggerFactory.getLogger(WorkTaskServiceImpl.class);




    /**
     * 保存工作任务
     * 新增工作任务 建立与点位关联
     * 更新工作任务
     *
     * @param taskDto
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public long save(TaskDto taskDto) {
        long id = 0;
        long spotId = taskDto.getSpotId();
        Task task = new Task();
        BeanUtils.copyProperties(taskDto, task);
        if (task.getId() == 0) {
            id = this.insert(task);
            SpotAndTask st = new SpotAndTask();
            st.setCreateTime(new Date());
            st.setSpotId(spotId);
            st.setTaskId(id);
            stService.save(st);
        } else {
            id = this.update(task);
        }
        return id;
    }

    /**
     * 根据点位id,删除相关工作任务
     *
     * @param spotId
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void deleteBySpotId(long spotId) {
        List<Long> taskIds = stService.queryBySpotId(spotId);
        for (int i = 0; i < taskIds.size(); i++) {
            wtRepository.deleteAllById(taskIds.get(i));
        }
    }

    /**
     * 根据任务ID，删除工作任务
     * 关联： spotAndTask
     *       TaskAndForm
     *       TaskAndDevice
     *
     * @param
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void delete(long spotId ,long id) {
        SpotAndTask st = new SpotAndTask();
        st.setTaskId(id);
        st.setSpotId(spotId);
        // 删除关联
        stService.deleteByTaskId(st);
        // 删除工作任务
        wtRepository.deleteAllById(id);
        // 异步删除与巡检内容 、设备的关联
        workTaskAsynService.deleteRelationInspect(id);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public long insert(Task task) {
        task.setCreateTime(new Date());
        Task newTask = wtRepository.save(task);
        return newTask.getId();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public long update(Task task) {
        task.setUpdateTime(new Date());
        wtRepository.save(task);
        return 0;
    }

    /**
     * 查询点位下的工作任务详情
     *
     * @param spotId
     * @return
     */
    @Override
    public List<Task> queryBySpotId(long spotId) {
        List<Long> taskIds = stService.queryBySpotId(spotId);
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < taskIds.size(); i++) {
            taskList.add(wtRepository.getOne(taskIds.get(i)));
        }
        return taskList;
    }

    /**
     * 根据点位id与taskName模糊查询
     * @param spotId
     * @param taskName
     * @return
     */
    @Override
    public List<Task> queryBySpotIdAndTaskName(long spotId, String taskName) {
        List<Long> taskIds = stService.queryBySpotId(spotId);

        return wtRepository.findByTaskIdInAndNameLike(taskIds,taskName);
    }

    /**
     * 根据点位id 与taskIds 查询,返回点位任务
     * @param spotId
     * @param taskIds
     * @return
     */
    @Override
    public List<SpotTaskShow> queryBySpotIdAndTaskIdIn(long spotId, List<Long> taskIds) {
       List<SpotTaskShow> returnList = new ArrayList<SpotTaskShow>();
       Spot spot = spotRepository.findSpotById(spotId);
        List<Task>  taskList = wtRepository.findByTaskIdIn(taskIds);
        for(int i = 0; i< taskList.size(); i++){
            SpotTaskShow st = new SpotTaskShow();
            st.setSpotId(spot.getId());
            st.setName(spot.getName());
            st.setFullName(spot.getFullName());
            st.setTaskId(taskList.get(i).getId());
            st.setTaskName(taskList.get(i).getName());
            returnList.add(st);
        }
        return returnList;
    }

    /**
     * 获取工作任务内容详情， 包括巡检内容或巡检模版列表，task-element 与设备列表
     *
     * @param taskId
     * @return
     */
    @Override
    public TaskDetailDto getContents(long taskId , int type) {
        TaskDetailDto taskDetailDto = new TaskDetailDto();
        List<InspectionContent> data = new ArrayList<>();
        List<BusinessDeviceDto> devices = new ArrayList<>();
        if(PollingType.COLLIGATE == type){
            // 综合巡检内容
            List<Long> formElementIds = taskFormService.getFormIds(taskId);
            if( formElementIds.size() > 0){
                data = inspectContentService.queryByFormElementIds(formElementIds);
            }
        }else if(PollingType.DEVICE == type){
            List<Long> deviceIds =  taskFormService.findByTaskIdAndType(taskId, type);
            if(deviceIds.size() > 0){
                for(int i = 0; i < deviceIds.size(); i++) {
                    BusinessDeviceDto  deviceDto = businessDeviceService.findById(deviceIds.get(i));
                    devices.add(deviceDto);
                }
            }
        }
        taskDetailDto.setContents(data);
        taskDetailDto.setDeviceDtos(devices);
        return taskDetailDto;
    }

    /**
     * 工作任务，新增巡检模版
     *
     * @param taskFormDto
     */
    @Override
    public void saveTemplate(TaskFormDto taskFormDto) {
        long taskId = taskFormDto.getTaskId();
        List<Long> templateIds = taskFormDto.getTemplateIds();
        List<Long> deviceIds = new ArrayList<>();

        // 综合巡检
        if(PollingType.COLLIGATE == taskFormDto.getType()){
            for(int i = 0; i< templateIds.size(); i++){
                List<Long> elements =  templateAndElementService
                        .getElementIds(templateIds.get(i));
                for(int m = 0; m < elements.size();m++){
                    TaskAndForm tf = new TaskAndForm();
                    tf.setTaskId(taskId);
                    tf.setType(PollingType.COLLIGATE);
                    tf.setFormElementId(elements.get(m));
                    taskFormService.save(tf);
                }
            }
            // 设备巡检
        } else if (PollingType.DEVICE == taskFormDto.getType()){
            deviceIds = taskFormDto.getDeviceIds();
            for(int i = 0; i < deviceIds.size(); i++){
                for(int m = 0; m < templateIds.size(); m++){
                    List<Long> elements =  templateAndElementService
                            .getElementIds(templateIds.get(i));
                    for(int n = 0; n < elements.size(); n++){
                        TaskAndForm tf = new TaskAndForm();
                        tf.setTaskId(taskId);
                        tf.setType(PollingType.DEVICE);
                        tf.setDeviceId(deviceIds.get(i));
                        tf.setFormElementId(elements.get(m));
                        taskFormService.save(tf);
                    }
                }
            }
        }
    }

    /**
     * 新增巡检内容
     * @param taskFormDto
     */
    @Override
    public void saveContents(TaskFormDto taskFormDto) {
        long taskId = taskFormDto.getTaskId();
        FormElementDto elementDto = taskFormDto.getFormElementDto();
        long id =  templateService.addContents(elementDto);
        TaskAndForm tf = new TaskAndForm();
        tf.setTaskId(taskId);
        tf.setFormElementId(id);
        if(PollingType.COLLIGATE == taskFormDto.getType()){
            // 综合巡检
            tf.setType(PollingType.COLLIGATE);
            taskFormService.save(tf);

        }else if(PollingType.DEVICE == taskFormDto.getType()){
            // 设备巡检
            tf.setType(PollingType.DEVICE);
            List<Long> deviceIds = taskFormDto.getDeviceIds();
            for(int i = 0; i < deviceIds.size(); i++){
                tf.setDeviceId(deviceIds.get(i));
                taskFormService.save(tf);
            }
        }
    }

    /**
     * 删除工作任务下 某一或某些 巡检内容
     *
     * @param taskFormDto
     */
    @Override
    public void batchInspects(TaskFormDto taskFormDto) {

        List<Long> ids = taskFormDto.getElementIds();
        for (int i = 0; i < ids.size(); i++) {
            TaskAndForm taskAndForm = new TaskAndForm();
            taskAndForm.setTaskId(taskFormDto.getTaskId());
            taskAndForm.setFormElementId(ids.get(i));
            taskFormService.delete(taskAndForm);
        }
    }

    /**
     * 分页条件查询任务
     * @param specification
     * @return
     */
    @Override
    public Page<Task> findBySpotId(Specification<Task> specification) {
        return wtRepository.findAll(specification, JpaUtils.getPageRequest());
    }

    @Override
    public Page<Task> findAllByTaskIdIn(List<Long> taskIds, Pageable pageable) {
       return wtRepository.findAllByTaskIdIn(taskIds,pageable);
    }

    @Override
    public List<InspectionContent> queryByTaskIdAndDeviceId(long taskId, long deviceId) {
        List<InspectionContent> contentList = new ArrayList<>();
        List<Long> formElementIds = taskFormService.findFormByTaskIdAndDeviceId(taskId,deviceId);
        if(formElementIds.size() > 0){
          contentList =  inspectContentService.queryByFormElementIds(formElementIds);
        }
       return  contentList;
    }

    /**
     * 工作详情分页查询
     * @param taskId
     * @param type
     * @param pageable
     * @return
     */
    @Override
    public TaskDetailDto getContentsByPage(long taskId, int type, Pageable pageable) {
        List<InspectionContent> contents = new ArrayList<>();
        List<Long> elements = new ArrayList<>();
        TaskDetailDto taskDetailDto = new TaskDetailDto();
        if(type == PollingType.COLLIGATE){
            // 综合巡检
            elements = taskFormService.findElementByTaskIdAndType(taskId,type);
            if(elements.size() > 0){
                Page<InspectionContent> page =  inspectContentService.findAllByElementIdIn(elements,pageable);
                taskDetailDto.setInspectionContentPage(page);
            }
        }else if(type == PollingType.DEVICE){
            // 设备巡检
            Page<TaskAndDevice> devicePage =  taskDeviceService.findAllByTaskId(taskId, pageable);
            taskDetailDto.setTaskAndDevicePage(devicePage);
        }

        return taskDetailDto;
    }

    /**
     * 解绑工作任务与巡检内容
     * @param taskFormDto
     */
    @Override
    public void deleteContents(TaskFormDto taskFormDto) {
        long taskId = taskFormDto.getTaskId();
        int type = taskFormDto.getType();

        if(type == PollingType.COLLIGATE) {
            // 综合巡检解绑
            List<Long> elements = taskFormDto.getElementIds();
            if (elements.size() > 0) {
                for (int i = 0; i < elements.size(); i++) {
                    taskFormService.deleteByTaskIdAndTypeAndFormElementId(taskId, type, elements.get(i));
                }
            }
        }else if(type == PollingType.DEVICE){
                // 设备巡检
                List<Long> deviceIds = taskFormDto.getDeviceIds();
                List<Long> elementIds = taskFormDto.getElementIds();
                if(deviceIds.size()> 0 && elementIds.size() > 0){
                    for(int m = 0; m < deviceIds.size();m++){
                        taskFormRepository.deleteByTaskIdAndDeviceIdAndFormElementIdIn(taskId,deviceIds.get(m),elementIds);
                    }
                }else{
                    logger.debug("device的size"+deviceIds.size()+"elementIds的size"+elementIds.size());
                }
        }
    }

    @Override
    public Page<Task> findAllByTaskIdInAndNameLike(List<Long> taskIds, String name, Pageable pageable) {
        return wtRepository.findAllByTaskIdInAndNameLike(taskIds,name,pageable);
    }
}
