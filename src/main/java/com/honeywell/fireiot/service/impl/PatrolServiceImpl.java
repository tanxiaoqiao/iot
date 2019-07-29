
package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dao.PatrolDao;
import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.job.AfterMsgJob;
import com.honeywell.fireiot.job.CheckJob;
import com.honeywell.fireiot.job.SendMsgJob;
import com.honeywell.fireiot.repository.*;
import com.honeywell.fireiot.service.PatrolService;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.utils.Pagination;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Service
public class PatrolServiceImpl implements PatrolService {

    private static String START_JOB = "startJob";
    private static String END_JOB = "endJob";
    private static String PRE_REMIND = "preRemind";
    private static String AFTER_REMIND = "afterRemind";

    @Autowired
    PatrolRepository patrolRepository;

    @Autowired
    PatrolDao patrolDao;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    InspectionResultRepository inspectionResultRepository;

    @Autowired
    PatrolSpotInspectRepository patrolSpotInspectRepository;

    @Autowired
    BusinessDeviceRepository businessDeviceRepository;

    @Autowired
    SpotRepository spotRepository;

    @Autowired
    SpotAndTaskRepository spotAndTaskRepository;

    @Autowired
    Scheduler scheduler;

    @Autowired
    WorkTeamService workTeamService;


    @Override
    public List<Spot> findone(Long patrolId) {
        List<PatrolSpotInspect> list = patrolSpotInspectRepository.findByPatrolId(patrolId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<Spot> sdvList = list.stream().map(l -> {
            Spot spot = spotRepository.findById(l.getSpotId()).orElse(null);
            return spot;
        }).collect(Collectors.toList());
        sdvList.removeAll(Collections.singleton(null));
        return sdvList;
    }


    @Override
    public List<InspectionResult> findSpotOne(Long patrolId, Long spotId) {
        PatrolSpotInspect spi = patrolSpotInspectRepository.findByPatrolIdAndAndSpotId(patrolId, spotId).orElse(null);
        List<InspectionResult> irs = inspectionResultRepository.findCommonByIds(spi.getInspectIds());
        return irs;
    }

    @Override
    public List<InspectionResult> findDeviceOne(Long patrolId, Long spotId, Long deviceId) {
        List<InspectionResult> list = inspectionResultRepository.findBySpotIdAndDeviceId(deviceId, spotId, patrolId);
        return list;
    }

    @Override
    public List<BusinessDevice> findSpotDevice(Long patrolId, Long spotId) {
        PatrolSpotInspect spi = patrolSpotInspectRepository.findByPatrolIdAndAndSpotId(patrolId, spotId).orElse(null);
        if (spi == null || CollectionUtils.isEmpty(spi.getInspectIds())) {
            return null;
        }
        List<Long> deviceIds = inspectionResultRepository.findDeviceByIds(spi.getInspectIds());
        List<BusinessDevice> bds = businessDeviceRepository.findAllById(deviceIds);
        return bds;
    }


    @Override
    public Pagination<Patrol> findByCondition(PatrolCondition condition) {
        Pagination<Patrol> patrols = patrolDao.findByCondition(condition);
        return patrols;
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateStatus(Long patrolId) {
        Patrol patrol = patrolRepository.findById(patrolId).orElse(null);
        //巡检状态 0未巡检 1进行中 2 按时完成 3延期完成 4未开始 5补检
        //判断个数
        String today = DateFormatUtils.format(new Date(), "HH:mm:ss");
        Timestamp updatetime = Timestamp.valueOf(today);
        Timestamp endtime = patrol.getPreEndTime();
        int inspected = patrol.getNormalNums() + patrol.getExceptionNums();
        int i = patrol.getSpotCount() - inspected - patrol.getSupplementNums();
        patrol.setMissNums(i);
        if (inspected < patrol.getSpotCount()) {
            //判断时间
            if (updatetime.getTime() <= endtime.getTime()) {
                patrol.setStatus(1);
            } else {
                patrol.setStatus(4);
            }
        } else if (inspected == patrol.getSpotCount()) {
            if (updatetime.getTime() <= endtime.getTime()) {
                patrol.setStatus(2);
            } else {
                patrol.setStatus(3);
            }
        }
    }


    @Override
    public PatrolReport report(String year) {
        int task = spotAndTaskRepository.findAllByYear(year);
        int finish = patrolRepository.findByYearAndStatus(year, 2);
        int delay = patrolRepository.findByYearAndStatus(year, 3);
        List<Object[]> byYear = patrolRepository.findByYear(year);
        int totalYear = Integer.parseInt(String.valueOf(byYear.get(0)[0]));
        int miss = Integer.parseInt(String.valueOf(byYear.get(0)[6]));
        int spot = Integer.parseInt(String.valueOf(byYear.get(0)[1]));
        int normal = Integer.parseInt(String.valueOf(byYear.get(0)[2]));
        int exe = Integer.parseInt(String.valueOf(byYear.get(0)[4]));
        int rep = Integer.parseInt(String.valueOf(byYear.get(0)[3]));
        int supp = Integer.parseInt(String.valueOf(byYear.get(0)[5]));
        int last = totalYear - finish - delay;
        String start = year + "-01";
        String end = year + "-12";
        //巡检状态2，3
        List<Object[]> monthReport = patrolRepository.findByMonthBetween(start, end);
        List monthReports = new ArrayList<MonthPatrol>();
        for (Object[] o : monthReport) {
            String s = String.valueOf(o[0]).split("-")[1];
            int month = Integer.parseInt(s);
            int total = Integer.parseInt(String.valueOf(o[01]));
            //完成
            int normalCount = patrolRepository.findByMonthAndStatus(String.valueOf(o[0]), 2);
            //延期完成
            int terminalCount = patrolRepository.findByMonthAndStatus(String.valueOf(o[0]), 3);
            List<Object[]> mo = patrolRepository.findByMonth(String.valueOf(o[0]));
            int missMonth = Integer.parseInt(String.valueOf(mo.get(0)[5]));
            int spotCount = Integer.parseInt(String.valueOf(mo.get(0)[0]));
            int normalMonth = Integer.parseInt(String.valueOf(mo.get(0)[1]));
            int exeMonth = Integer.parseInt(String.valueOf(mo.get(0)[3]));
            int repMonth = Integer.parseInt(String.valueOf(mo.get(0)[2]));
            int suppMonth = Integer.parseInt(String.valueOf(mo.get(0)[4]));
            Integer unfishMonth = total - normalCount - terminalCount;
            MonthPatrol mc = new MonthPatrol(month, total, normalCount, terminalCount,
                    unfishMonth, spotCount, normalMonth, exeMonth, repMonth, missMonth, suppMonth);
            monthReports.add(mc);
        }
        PatrolReport patrolReport = new PatrolReport(totalYear, spot, task, monthReports, finish, delay, last, normal, exe, rep, miss, supp);
        return patrolReport;
    }

    @Override
    public Boolean add(Patrol patrol) {
        Patrol save = patrolRepository.save(patrol);
        if (patrol.getWorkTeamIds() != null) {
            List<EmpPatrol> collect = new ArrayList<>();
            patrol.getWorkTeamIds().forEach(w -> {
                WorkTeamDto one = workTeamService.findOne(w);
                one.getAllIds().forEach(m -> {
                    EmpPatrol ep = new EmpPatrol();
                    ep.setEmployeeId(m);
                    collect.add(ep);
                });
            });
            patrol.setEmpPatrols(collect);
        }
        if (patrol.getPersonIds() != null) {
            List<EmpPatrol> collect = patrol.getPersonIds().stream().map(p -> {
                EmpPatrol ep = new EmpPatrol();
                ep.setEmployeeId(p);
                return ep;
            }).collect(Collectors.toList());
            patrol.setEmpPatrols(collect);
        }
        patrolRepository.save(patrol);
        //提供给spot
        if (patrol.getSpotTask() == null) {
            throw new BusinessException(ErrorEnum.SPOT_NOT_EXIST);
        }
        List<PatrolSpotInspect> psi = save.getSpotTask().stream().map(s -> {
            PatrolSpotInspect pai = new PatrolSpotInspect();
            pai.setPatrolId(save.getId());
            pai.setSpotId(s.getSpotId());
            return pai;
        }).collect(Collectors.toList());
        patrolSpotInspectRepository.saveAll(psi);
        String s = save.getId().toString();

        JobDetail preRemind =
                JobBuilder.newJob(SendMsgJob.class)
                        .withIdentity(PRE_REMIND, s)
                        .build();
        JobDetail endRemind =
                JobBuilder.newJob(AfterMsgJob.class)
                        .withIdentity(AFTER_REMIND, s)
                        .build();

        JobDetail update =
                JobBuilder.newJob(CheckJob.class)
                        .withIdentity(END_JOB, s)
                        .build();

        SimpleTrigger startt = (SimpleTrigger) newTrigger()
                .withIdentity(START_JOB, s)
                .startAt(patrol.getPreStartTime())
                .build();
        SimpleTrigger endt = (SimpleTrigger) newTrigger()
                .withIdentity(END_JOB, s)
                .startAt(patrol.getPreEndTime())
                .build();

        try {
            scheduler.start();
            scheduler.scheduleJob(update, endt);
            //构建消息
            scheduler.scheduleJob(preRemind, startt);
            scheduler.scheduleJob(endRemind, endt);
        } catch (
                SchedulerException e) {
            e.printStackTrace();
        }

        return true;
}
}
