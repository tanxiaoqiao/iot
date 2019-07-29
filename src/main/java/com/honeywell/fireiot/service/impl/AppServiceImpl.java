package com.honeywell.fireiot.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.honeywell.fireiot.dto.FormElementDto;
import com.honeywell.fireiot.dto.SpotVo;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.repository.EmpPatrolRepository;
import com.honeywell.fireiot.service.*;
import com.honeywell.fireiot.utils.ListPage;
import com.honeywell.fireiot.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppServiceImpl
 * @author: dexter
 * @create: 2019-03-19 13:50
 * @description:
 **/
@Service
public class AppServiceImpl implements AppService {

    // taskAndForm 类型是综合表单
    public static final int GENERAL_TYPE = 0;
    // taskAndForm 类型是设备表单
    public static final int DEVICE_TYPE = 1;

    @Autowired
    private SpotService spotService;

    @Autowired
    private WorkTaskService workTaskService;

    @Autowired
    private TaskFormService taskFormService;

    @Autowired
    private FormElementService formElementService;

    @Autowired
    private EmpPatrolRepository empPatrolRepository;

    @Autowired
    private BusinessDeviceService businessDeviceService;

    @Override
    public ListPage<AppPatrolVO> download(String employeeId, Integer pageSize, Integer pageNum) {
        List<EmpPatrol> empPatrolList = empPatrolRepository.findByEmployeeId(employeeId);
        if (CollectionUtils.isEmpty(empPatrolList)) {
            return null;
        }

        List<AppPatrolVO> appPatrolVOList = new ArrayList<>();

        empPatrolList.forEach(empPatrol -> {
            List<Patrol> patrols = empPatrol.getPatrols();
            if (!CollectionUtils.isEmpty(patrols)) {
                patrols.forEach(patrol -> {
                    // 未巡检
                    if (patrol.getStatus() == 0) {
                        AppPatrolVO appPatrolVO = new AppPatrolVO();
                        BeanUtils.copyProperties(patrol, appPatrolVO);
                        appPatrolVO.setPatrolId(patrol.getId());
                        List<Long> spotIds = patrol.getSpotTask().stream().map(SpotVo::getSpotId).collect(Collectors.toList());
                        List<AppSpotVO> appSpotVOList = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(spotIds)) {
                            spotIds.forEach(spotId -> {
                                AppSpotVO appSpotVO = getSpotTasks(spotId);
                                if (appPatrolVO != null) {
                                    appSpotVOList.add(appSpotVO);
                                }
                            });
                        }
                        appPatrolVO.setAppSpotVOs(appSpotVOList);

                        appPatrolVOList.add(appPatrolVO);
                    }
                });
            }
        });

        ListPage<AppPatrolVO> appPatrolVOListPage =
                new ListPage<>(appPatrolVOList)
                        .getPagedList(pageNum, pageSize);
        appPatrolVOListPage.setTotalCount(
                CollectionUtils.isEmpty(appPatrolVOList) ? 0 : appPatrolVOList.size());

        return appPatrolVOListPage;
    }

    private AppSpotVO getSpotTasks(Long spotId) {
        Spot spot = spotService.getSpotById(spotId);
        if (spot != null) {
            AppSpotVO appSpotVO = new AppSpotVO();
            BeanUtils.copyProperties(spot, appSpotVO);
            // 获取taskList
            List<Task> taskList = workTaskService.queryBySpotId(spotId);
            if (!CollectionUtils.isEmpty(taskList)) {
                List<AppTaskFormVO> taskFormVOList = new ArrayList<>();
                taskList.forEach(task -> {
                    AppTaskFormVO appTaskFormVO = new AppTaskFormVO();
                    BeanUtils.copyProperties(task, appTaskFormVO);
//                    List<Long> formIds = taskFormService.getFormIds(task.getId());
                    List<TaskAndForm> taskAndFormList = taskFormService.findByTaskId(task.getId());
                    if (!CollectionUtils.isEmpty(taskAndFormList)) {
                        List<TaskAndForm> generalTaskAndFormList = taskAndFormList
                                .stream()
                                .filter(taskAndForm -> taskAndForm.getType() == GENERAL_TYPE)
                                .collect(Collectors.toList());
                        List<TaskAndForm> deviceTaskAndFormList = taskAndFormList
                                .stream()
                                .filter(taskAndForm -> taskAndForm.getType() == DEVICE_TYPE)
                                .collect(Collectors.toList());

                        if (!CollectionUtils.isEmpty(generalTaskAndFormList)) {
                            AppGeneralTaskVO appGeneralTaskVO = generalTask(generalTaskAndFormList);
                            appTaskFormVO.setAppGeneralTaskVO(appGeneralTaskVO);
                        }

                        if (!CollectionUtils.isEmpty(deviceTaskAndFormList)) {
                            Multimap<Long, TaskAndForm> deviceMultimap = ArrayListMultimap.create();
                            for (TaskAndForm taskAndForm : deviceTaskAndFormList) {
                                deviceMultimap.put(taskAndForm.getDeviceId(), taskAndForm);
                            }

                            appTaskFormVO.setAppDeviceTaskVOList(deviceTask(deviceMultimap));

                        }

                        taskFormVOList.add(appTaskFormVO);
                    }
                });
                appSpotVO.setAppTaskFormVOs(taskFormVOList);
            }
            return appSpotVO;
        } else {
            return null;
        }
    }

    private AppGeneralTaskVO generalTask(List<TaskAndForm> generalTaskAndFormList) {
        AppGeneralTaskVO appGeneralTaskVO = new AppGeneralTaskVO();
        List<Long> formIds = generalTaskAndFormList
                .stream()
                .map(TaskAndForm::getFormElementId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(formIds)) {
            List<FormElementDto> formElementDtoList = getFormElements(formIds);
            appGeneralTaskVO.setFormElements(formElementDtoList);
        }
        return appGeneralTaskVO;
    }

    private List<AppDeviceTaskVO> deviceTask(Multimap<Long, TaskAndForm> deviceMultimap) {
        List<AppDeviceTaskVO> appDeviceTaskVOList = Lists.newArrayList();
        Map<Long, Collection<TaskAndForm>> deviceMap = deviceMultimap.asMap();
        for (Map.Entry<Long, Collection<TaskAndForm>> entry : deviceMap.entrySet()) {
            appDeviceTaskVOList.add(
                    AppDeviceTaskVO
                            .builder()
                            .deviceId(entry.getKey())
                            .deviceName(businessDeviceService.findById(entry.getKey()).getDeviceLabel())
                            .formElements(
                                    getFormElements(
                                            entry.getValue()
                                                    .stream()
                                                    .map(TaskAndForm::getFormElementId)
                                                    .collect(Collectors.toList())
                                    )
                            )
                            .build()
            );
        }
        return appDeviceTaskVOList;
    }

    private List<FormElementDto> getFormElements(List<Long> formIds) {
        List<FormElementDto> formElementDtos = new ArrayList<>();
        formIds.forEach(formId -> {
            Optional<FormElement> optionalFormElement = formElementService.findById(formId);
            if (optionalFormElement.isPresent()) {
                FormElementDto formElementDto = formElementService.toDto(optionalFormElement.get());
                formElementDtos.add(formElementDto);
            }
        });
        return formElementDtos;
    }

}
