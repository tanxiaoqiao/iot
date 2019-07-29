package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.constant.AbstractAppPatrolSpotStatus;
import com.honeywell.fireiot.dto.AppElementDTO;
import com.honeywell.fireiot.dto.AppFaultDTO;
import com.honeywell.fireiot.dto.AppSpotDTO;
import com.honeywell.fireiot.dto.AppTaskDTO;
import com.honeywell.fireiot.entity.AppPatrolSpot;
import com.honeywell.fireiot.entity.InspectionResult;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.entity.PatrolSpotInspect;
import com.honeywell.fireiot.repository.AppPatrolSpotRepository;
import com.honeywell.fireiot.repository.PatrolRepository;
import com.honeywell.fireiot.repository.PatrolSpotInspectRepository;
import com.honeywell.fireiot.service.AppPatrolSpotService;
import com.honeywell.fireiot.service.InspectionService;
import com.honeywell.fireiot.service.WorkorderService;
import com.honeywell.fireiot.vo.SaveWorkorderVo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppPatrolSpotServiceImpl
 * @author: dexter
 * @create: 2019-04-04 13:59
 * @description:
 **/
@Service
@Slf4j
public class AppPatrolSpotServiceImpl implements AppPatrolSpotService {

    private static final String INIT_TIME = "1970-01-01 00:00:00";

    private static final Long INIT_LONG = DateTime
            .parse(INIT_TIME, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
            .getMillis();

    @Autowired
    private AppPatrolSpotRepository appPatrolSpotRepository;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private PatrolRepository patrolRepository;

    @Autowired
    private PatrolSpotInspectRepository patrolSpotInspectRepository;

    @Autowired
    private WorkorderService workorderService;

    @Override
    public String save(AppPatrolSpot appPatrolSpot) {
        appPatrolSpotRepository.save(appPatrolSpot);
        if (StringUtils.isEmpty(appPatrolSpot.getId())) {
            return null;
        } else {
            analyze(appPatrolSpot);
            return appPatrolSpot.getId();
        }
    }

    @Override
    @Async
    public void analyze(AppPatrolSpot appPatrolSpot) {
        AtomicInteger supplementNums = new AtomicInteger(0);

        final AtomicReference<Timestamp>[] actStartTimeArray = new AtomicReference[]{new AtomicReference<>(new Timestamp(INIT_LONG))};
        final AtomicReference<Timestamp>[] actEndTimeArray = new AtomicReference[]{new AtomicReference<>(new Timestamp(INIT_LONG))};
        // 根据spotStatus统计
        AtomicInteger normalNums = new AtomicInteger(0);
        AtomicInteger exceptionNums = new AtomicInteger(0);

        // 报修个数
        // 小于等于异常个数，针对一个spot报修数目 <= 1
        Set<Long> spotRepairSet = new ConcurrentSkipListSet<>();

        List<AppSpotDTO> spotList = appPatrolSpot.getSpotList();
        if (!isEmpty(spotList)) {
            spotList.forEach(spot -> {
                List<InspectionResult> inspectionResultList = new ArrayList<>();
                List<AppTaskDTO> taskList = spot.getTaskList();
                List<AppFaultDTO> faultList = spot.getFaultList();
                if (!isEmpty(taskList)) {
                    taskList.forEach(task -> {
                        List<AppElementDTO> elementList = task.getElementList();
                        if (!isEmpty(elementList)) {
                            elementList.stream().forEach(element -> {
                                InspectionResult inspectionResult =
                                        InspectionResult.builder()
                                                .name(element.getName())
                                                .value(element.getValue())
                                                .status(element.getStatus())
                                                .abnormalResult(element.getAbnormalResult())
                                                .abnormalMethod(element.getAbnormalMethod())
                                                .fault(element.getFaultDescription())
                                                .operator(element.getOperator())
                                                .images(element.getImages())
                                                .spotId(spot.getSpotId())
                                                .patrolId(appPatrolSpot.getPatrolId())
                                                .type(element.getType())
                                                .deviceId(element.getDeviceId())
                                                .build();

                                // 若超时，则漏检数+1
                                if (element.getIsOvertime()) {
                                    supplementNums.incrementAndGet();
                                }
                                // 保存到wo_inspect_result
                                inspectionService.save(inspectionResult);
                                inspectionResultList.add(inspectionResult);

    //                                if (inspectionResult.getAbnormalMethod() == AbstractAppPatrolSpotStatus.FAULT_NEED_TO_BE_FIXED) {
    //                                    // 直接调用workorder接口创建工单
    //                                    // 报修个数
    //                                    // 针对一个点位报修数目 <= 1，即只要有任何一个Element的处理方式是报障，则该点位保修数目即为1
    //                                    if (!spotRepairSet.contains(spot.getSpotId())) {
    //                                        spotRepairSet.add(spot.getSpotId());
    //                                    }
    //
    //                                    SaveWorkorderVo saveWorkorderVo = new SaveWorkorderVo();
    //                                    saveWorkorderVo.setCreator(element.getCreator());
    //                                    saveWorkorderVo.setCreatorName(element.getCreatorName());
    //                                    saveWorkorderVo.setWorkTeamId(element.getWorkTeamId());
    //                                    saveWorkorderVo.setWorkTeamName(element.getWorkTeamName());
    //                                    saveWorkorderVo.setPreStartTime(element.getPreStartTime());
    //                                    saveWorkorderVo.setPreEndTime(element.getPreEndTime());
    //                                    saveWorkorderVo.setType(2);
    //                                    saveWorkorderVo.setDescription(element.getFaultDescription());
    //                                    saveWorkorderVo.setTitle(element.getTitle());
    //                                    saveWorkorderVo.setAuditId(element.getAuditId());
    //                                    saveWorkorderVo.setAuditName(element.getAuditName());
    //                                    try {
    //                                        workorderService.addWorkorder(saveWorkorderVo, null);
    //                                    } catch (IOException e) {
    //                                        log.error(e.getMessage());
    //                                    }
    //                                }
    //
                            });
                        }
                    });


                    List<Timestamp> taskFillTimeList = taskList
                            .stream()
                            .map(AppTaskDTO::getFillTime)
                            .collect(Collectors.toList());
                    taskFillTimeList.sort(Timestamp::compareTo);
                    actStartTimeArray[0] = new AtomicReference<>(taskFillTimeList.get(0));
                    actEndTimeArray[0] = new AtomicReference<>(taskFillTimeList.get(taskFillTimeList.size() - 1));
                }
                // 故障工单处理
                if (!isEmpty(faultList)) {
                    faultList.stream().forEach(fault -> {
                        SaveWorkorderVo saveWorkorderVo = new SaveWorkorderVo();
                        saveWorkorderVo.setTitle(fault.getTitle());
                        saveWorkorderVo.setWorkTeamName(fault.getWorkTeamName());
                        saveWorkorderVo.setWorkTeamId(fault.getWorkTeamId());
                        saveWorkorderVo.setType(2);
                        saveWorkorderVo.setDeviceIds(fault.getDeviceIds());
                        saveWorkorderVo.setLocationIds(fault.getLocationIds());
                        saveWorkorderVo.setDescription(fault.getDescription());
                        saveWorkorderVo.setSaveAuto(false);
                        saveWorkorderVo.setAuditId(fault.getAuditId());
                        saveWorkorderVo.setAuditName(fault.getAuditName());
                        try {
                            workorderService.addWorkorder(saveWorkorderVo, null);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
                }

                // 点位异常个数
                if (spot.getSpotStatus() == AbstractAppPatrolSpotStatus.SPOT_ABNORMAL) {
                    exceptionNums.incrementAndGet();
                }
                // 点位正常个数
                if (spot.getSpotStatus() == AbstractAppPatrolSpotStatus.SPOT_NORMAL) {
                    normalNums.incrementAndGet();
                }

                List<Long> inspectIds = inspectionResultList
                        .stream()
                        .map(InspectionResult::getId)
                        .collect(Collectors.toList());

                PatrolSpotInspect patrolSpotInspect = patrolSpotInspectRepository
                        .findByPatrolIdAndAndSpotId(appPatrolSpot.getPatrolId(), spot.getSpotId()).orElse(new PatrolSpotInspect());
                // 不存在此记录
                if (patrolSpotInspect.getId() <= 0) {
                    patrolSpotInspect.setSpotId(spot.getSpotId());
                    patrolSpotInspect.setCreateTime(DateTime.now().toDate());
                }
                patrolSpotInspect.setInspectIds(new ArrayList<>(inspectIds));

                patrolSpotInspectRepository.save(patrolSpotInspect);

            });
        }


        // 更新相应patrol信息
        Optional<Patrol> optionalPatrol = patrolRepository.findById(appPatrolSpot.getPatrolId());
        if (optionalPatrol.isPresent()) {
            Patrol patrol = optionalPatrol.get();
            patrol.setSupplementNums(supplementNums.get());
            patrol.setNormalNums(normalNums.get());
            patrol.setExceptionNums(exceptionNums.get());
            patrol.setRepairNums(spotRepairSet.size());
            patrol.setActStartTime(actStartTimeArray[0].get());
            patrol.setActEndTime(actEndTimeArray[0].get());
            patrolRepository.save(patrol);
        }
    }
}
