package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.TaskDetailDto;
import com.honeywell.fireiot.dto.TaskDeviceDto;
import com.honeywell.fireiot.dto.TaskDto;
import com.honeywell.fireiot.dto.TaskFormDto;
import com.honeywell.fireiot.entity.InspectionContent;
import com.honeywell.fireiot.entity.Task;
import com.honeywell.fireiot.service.SpotAndTaskService;
import com.honeywell.fireiot.service.SpotService;
import com.honeywell.fireiot.service.TaskDeviceService;
import com.honeywell.fireiot.service.WorkTaskService;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@Api(tags = "工作任务")
public class TaskController {
    /**
     *  工作任务service（对应操作实体Task）
     */
    @Autowired
    private WorkTaskService wtService;
    @Autowired
    private SpotService spotService;
    @Autowired
    private TaskDeviceService taskDeviceService;
    @Autowired
    private SpotAndTaskService spotAndTaskService;


    @GetMapping("/get")
    @ApiOperation(value = "根据点位ID，查询工作任务")
    public ResponseObject getTask(@RequestParam("spotId") long spotId) {
        if (spotId == 0) {
            return ResponseObject.fail(ErrorEnum.MISS_REQUEST_PARAMTER);
        }
        Boolean isExist = spotService.isExist(spotId);
        if (isExist == false) {
            return ResponseObject.fail(ErrorEnum.SPOT_NOT_EXIST);
        } else {
            List<Task> list = wtService.queryBySpotId(spotId);
            return ResponseObject.success(list);
        }
    }
    @GetMapping("/page")
    @ApiOperation(value = "点位分页查询工作任务")
    @JpaPage
    public ResponseObject getTaskByPage(@RequestParam("spotId") long spotId,@RequestParam("pi") int pi,@RequestParam("ps") int ps) {
       List<Long> taskIds =  spotAndTaskService.queryBySpotId(spotId);
        Page<Task> taskPage = null;
       if(taskIds.size()> 0){
           Pageable pageable = PageRequest.of(pi-1,ps);
           taskPage =  wtService.findAllByTaskIdIn(taskIds,pageable);
       }
        Pagination<Task> page = new Pagination<Task>((int) taskPage.getTotalElements(), taskPage.getContent());
       return ResponseObject.success(page);

    }

    /**
     * 获取工作任务所包含的巡检内容或模版巡检内容列表
     * @param id
     * @return
     */
    @GetMapping("/contents")
    @ApiOperation(value = "获取工作任务详情, 分页查询")
    public ResponseObject getDetail(@RequestParam("id") long id, @RequestParam("type") int type,@RequestParam("pi") int pi, @RequestParam("ps") int ps) {
        Pageable pageable = PageRequest.of(pi-1,ps);
        TaskDetailDto taskDetailDto = wtService.getContentsByPage(id,type,pageable);
//        Pagination<InspectionContent> page = new Pagination<InspectionContent>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(taskDetailDto);
    }
    @PostMapping("/save")
    @ApiOperation(value = "保存工作任务")
    public ResponseObject save(@RequestBody TaskDto taskDto) {
        if (taskDto.getSpotId() == 0) {
            return ResponseObject.fail(ErrorEnum.PARAM_IS_NULL);
        }
        long id = wtService.save(taskDto);
        return ResponseObject.success(id);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除工作任务")
    public ResponseObject deleteTask(@RequestParam("spotId")long spotId, @RequestParam("id") long id) {
        wtService.delete(spotId,id);
        return ResponseObject.success(null);
    }

    @PostMapping("/saveTemplate")
    @ApiOperation(value = "工作任务新增巡检模版")
    public ResponseObject saveTemplate(@RequestBody TaskFormDto taskFormDto) {
        wtService.saveTemplate(taskFormDto);
        return ResponseObject.success(null);
    }
    @PostMapping("/saveContents")
    @ApiOperation(value = "工作任务新增巡检内容")
    public ResponseObject saveContents(@RequestBody TaskFormDto taskFormDto){
        wtService.saveContents(taskFormDto);
        return ResponseObject.success(null);
    }

    @PostMapping("/saveDevice")
    @ApiOperation(value = "工作任务新增关联设备")
    public ResponseObject saveDevice(@RequestBody TaskDeviceDto taskDeviceDto) {
        taskDeviceService.save(taskDeviceDto);
        return ResponseObject.success(null);
    }

    @PostMapping("/batchDevice")
    @ApiOperation(value = "批量删除关联设备")
    public ResponseObject batchDeleteDevice(@RequestBody TaskDeviceDto taskDeviceDto) {
        taskDeviceService.batchDelete(taskDeviceDto);
        return ResponseObject.success(null);
    }
    @GetMapping("/DeviceContents")
    @ApiOperation(value ="获取设备下的相关巡检内容")
    public ResponseObject getContentByDeviceId(@RequestParam("taskId") long taskId, @RequestParam("deviceId") long deviceId){
        List<InspectionContent> list = wtService.queryByTaskIdAndDeviceId(taskId,deviceId);
        return ResponseObject.success(list);
    }
    @PostMapping("/deleteContents")
    @ApiOperation(value="与巡检内容解除绑定，指定taskId与type")
    public ResponseObject deleteDevices(@RequestBody TaskFormDto taskFormDto){
        wtService.deleteContents(taskFormDto);
        return ResponseObject.success(null);
    }
}
