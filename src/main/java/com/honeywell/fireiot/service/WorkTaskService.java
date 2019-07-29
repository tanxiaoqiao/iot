package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.SpotTaskShow;
import com.honeywell.fireiot.dto.TaskDetailDto;
import com.honeywell.fireiot.dto.TaskDto;
import com.honeywell.fireiot.dto.TaskFormDto;
import com.honeywell.fireiot.entity.InspectionContent;
import com.honeywell.fireiot.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface WorkTaskService {

     long save(TaskDto taskDto);
     void deleteBySpotId(long spotId);
     void delete(long spotId,long id);
     long insert(Task task);
     long update(Task task);
     List<Task> queryBySpotId(long spotId);
     List<Task> queryBySpotIdAndTaskName(long spotId, String taskName);
     List<SpotTaskShow> queryBySpotIdAndTaskIdIn(long spotId, List<Long> taskIds);
     TaskDetailDto getContents(long taskId , int type);
     void saveTemplate(TaskFormDto taskFormDto);
     void saveContents(TaskFormDto taskFormDto);
     void batchInspects(TaskFormDto taskFormDto);
     Page<Task> findBySpotId(Specification<Task> specification);
     Page<Task> findAllByTaskIdIn(List<Long> taskIds, Pageable pageable);
     List<InspectionContent> queryByTaskIdAndDeviceId(long taskId, long deviceId);
     TaskDetailDto getContentsByPage(long taskId,int type,Pageable pageable);
     void deleteContents(TaskFormDto taskFormDto);

     Page<Task> findAllByTaskIdInAndNameLike(List<Long> taskIds,String name,Pageable pageable);



}
