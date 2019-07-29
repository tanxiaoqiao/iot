package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.entity.Polling;
import com.honeywell.fireiot.entity.SpotAndTask;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.job.CreateIntervalPatrolJob;
import com.honeywell.fireiot.job.CreatePatrolJob;
import com.honeywell.fireiot.job.CreateUnregularPatrolJob;
import com.honeywell.fireiot.repository.PatrolRepository;
import com.honeywell.fireiot.repository.PollingRepository;
import com.honeywell.fireiot.service.EmployeeService;
import com.honeywell.fireiot.service.PollingService;
import com.honeywell.fireiot.service.WorkTaskService;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.utils.CronUtil;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Service
public class PollingServiceImpl implements PollingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollingServiceImpl.class);
    private static String CREATE_UNREGULAR_PATROL = "createunregularPatrol";
    private static String CREATE_PATROL = "createPatrol";


    private static final String DEFINE_HOLIDAY = "DEFINE_HOLIDAY";


    @Autowired
    PollingRepository pollingRepository;


    @Autowired
    PatrolRepository patrolRepository;


    @Autowired
    private Scheduler scheduler;

    @Autowired
    WorkTeamService workTeamService;

    @Autowired
    EmployeeService employeeService;

    @Value("${polling.hour}")
    private String hour;

    @Value("${polling.minute}")
    private String minute;


    @Autowired
    WorkTaskService workTaskService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Long addPolling(Polling polling) throws SchedulerException {
        Polling byName = pollingRepository.findByName(polling.getName());
        if (byName != null) {
            throw new BusinessException(ErrorEnum.POLLING_EXIST);
        }
        //按新的cronExpression表达式构建一个新的trigger
        TriggerBuilder<Trigger> tb = newTrigger();
        //使用节假日
//        if (polling.getUseHoliday()) {
//            Calendar c = scheduler.getCalendar(DEFINE_HOLIDAY);
//            if (c != null) {
//                tb.modifiedByCalendar(DEFINE_HOLIDAY);
//            }
//        }
        polling.setSpotCount(polling.getSpotTask().size());
        Polling save = pollingRepository.save(polling);
        String jobGroupName = save.getId().toString();
        //固定周期
        LOGGER.info("开始时间:{}", polling.getActiviteStart());
        JobKey starkey = null;
        if (polling.getRegularType() == 1) {
            TriggerKey startkey = TriggerKey.triggerKey(CREATE_PATROL, save.getId().toString());
            tb.withIdentity(startkey);
            starkey = new JobKey(CREATE_PATROL, jobGroupName);
            JobDetail startJob =
                    JobBuilder.newJob(CreatePatrolJob.class).withIdentity(starkey).build();
            //激活时间
            tb.startAt(polling.getActiviteStart());

            //失效时间
            TaskScheduleModel startCron = new TaskScheduleModel(hour, minute, polling.getJobType(),
                    polling.getDayOfWeeks(), polling.getDayOfMonths());
            //构建cron
            String jobStartCron = CronUtil.createCronExpression(startCron);
            LOGGER.info("每天生成patrol的定时任务时间:{}", jobStartCron);
            //表达式调度构建器(即任务执行的时间)  .withMisfireHandlingInstructionFireAndProceed()立即触发一次
            CronScheduleBuilder startCsb =
                    CronScheduleBuilder.cronSchedule(jobStartCron).withMisfireHandlingInstructionDoNothing();
            CronTrigger startCt = tb.withSchedule(startCsb).build();
            scheduler.start();
            scheduler.scheduleJob(startJob, startCt);
            //非周期
        } else if (polling.getRegularType() == 0) {
            for (int i = 0; i < polling.getUnRegularTimes().size(); i++) {
                String name = jobGroupName + "_" + i;
                starkey = new JobKey(CREATE_UNREGULAR_PATROL, name);
                JobDetail unregular =
                        JobBuilder.newJob(CreateUnregularPatrolJob.class).withIdentity(starkey).build();
                TriggerKey tr = new TriggerKey(CREATE_UNREGULAR_PATROL, name);
                tb.withIdentity(tr);
                String format = DateFormatUtils.format(polling.getUnRegularTimes().get(i).getUnRegularStart(), "yyyy-MM-dd");
                String s = format + " " + hour + ":" + minute + ":00";
                LOGGER.info("非周期开始时间:{}", s);
                SimpleScheduleBuilder ssb =
                        SimpleScheduleBuilder.simpleSchedule();
                SimpleTrigger sts =
                        tb.startAt(Timestamp.valueOf(s)).withSchedule(ssb).build();
                try {
                    scheduler.start();
                    scheduler.scheduleJob(unregular, sts);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
            //间隔周期
        } else if (polling.getRegularType() == 2) {
            TriggerKey startkey = TriggerKey.triggerKey(CREATE_PATROL, save.getId().toString());
            tb.withIdentity(startkey);
            starkey = new JobKey(CREATE_PATROL, jobGroupName);
            JobDetail startJob =
                    JobBuilder.newJob(CreateIntervalPatrolJob.class).withIdentity(starkey).build();
            tb.startAt(polling.getActiviteStart());
            //间隔月
            if (polling.getJobType() == 2) {
                //获得开始月份
                String format = DateFormatUtils.format(polling.getActiviteStart(), "yyyy-MM-dd HH:mm:ss");
                Integer month = Integer.valueOf(format.substring(5, 7));
                Integer day = Integer.valueOf(format.substring(8, 10));
                TaskScheduleModel startCron = new TaskScheduleModel(hour, minute, polling.getJobType(),
                        polling.getDayOfWeeks(), polling.getDayOfMonths());
                //构建cron
                String jobStartCron = CronUtil.createIntervalExpression(startCron, polling.getTimes(), day, month);
                LOGGER.info("每天生成patrol的间隔任务时间:{}", jobStartCron);
                //表达式调度构建器(即任务执行的时间)  .withMisfireHandlingInstructionFireAndProceed()立即触发一次
                CronScheduleBuilder startCsb =
                        CronScheduleBuilder.cronSchedule(jobStartCron).withMisfireHandlingInstructionDoNothing();
                CronTrigger startCt = tb.withSchedule(startCsb).build();
                scheduler.start();
                scheduler.scheduleJob(startJob, startCt);
            } else {
                SimpleScheduleBuilder ssb = null;
                if (polling.getJobType() == 0) {
                    //间隔日
                    ssb = SimpleScheduleBuilder.repeatHourlyForever(polling.getTimes() * 24).withMisfireHandlingInstructionNextWithRemainingCount();
                    LOGGER.info("每天生成patrol的间隔小时:{}", polling.getTimes() * 24);
                } else if (polling.getJobType() == 1) {
                    //间隔周
                    ssb = SimpleScheduleBuilder.repeatHourlyForever(polling.getTimes() * 24 * 7).withMisfireHandlingInstructionNextWithRemainingCount();
                    LOGGER.info("每天生成patrol的间隔小时:{}", polling.getTimes() * 24 * 7);
                }
                SimpleTrigger st = tb.withSchedule(ssb).build();
                scheduler.start();
                scheduler.scheduleJob(startJob, st);
            }
        }
        if (!polling.getActivated()) {
            scheduler.pauseJob(starkey);
        }
        return save.getId();
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean deletePolling(Long pollingId) throws SchedulerException {
        Polling byId = pollingRepository.findById(pollingId).orElse(null);

        if (byId == null) {
            throw new BusinessException(ErrorEnum.NO_POLLING);
        }
        String jobGroupName = byId.getId().toString();
        //非周期
        if (byId.getRegularType() == 0) {
            for (int i = 0; i < byId.getUnRegularTimes().size(); i++) {
                TriggerKey endKey = TriggerKey.triggerKey(CREATE_UNREGULAR_PATROL, jobGroupName + "_" + i);
                // 停止触发器
                try {
                    scheduler.pauseTrigger(endKey);
                    // 移除触发器
                    scheduler.unscheduleJob(endKey);
                    //更新trigger
                    // 删除任务
                    JobKey preJob = new JobKey(CREATE_UNREGULAR_PATROL, jobGroupName + "_" + i);
                    scheduler.deleteJob(preJob);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
            pollingRepository.deleteById(pollingId);
            return true;
        }
        TriggerKey endKey = TriggerKey.triggerKey(CREATE_PATROL, jobGroupName);
        // 停止触发器
        scheduler.pauseTrigger(endKey);
        // 移除触发器
        scheduler.unscheduleJob(endKey);
        //更新trigger

        // 删除任务
        JobKey preJob = new JobKey(CREATE_PATROL, jobGroupName);
        scheduler.deleteJob(preJob);
        pollingRepository.deleteById(pollingId);
        return true;
    }


    @Override
    public PollingDto findone (Long pollingId) {
        Polling polling = pollingRepository.findById(pollingId).orElse(null);
        List<String> workteamNames = polling.getWorkteamId().stream().map(l -> {
            WorkTeamDto one = workTeamService.findOne(l);
            return one.getTeamName();
        }).collect(Collectors.toList());
        List<String> remindteamNames = new ArrayList<>();
        List<String> personNames = new ArrayList<>();
        List<String> reminderNames = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(polling.getRemindteamId())) {
            remindteamNames = polling.getRemindteamId().stream().map(l -> {
                WorkTeamDto one = workTeamService.findOne(l);
                return one.getTeamName();
            }).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(polling.getPersonIds())) {
            personNames = polling.getPersonIds().stream().map(l -> {
                EmployeeDto ed = employeeService.findById(l);
                return ed.getName();
            }).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(polling.getReminderIds())) {
            reminderNames = polling.getReminderIds().stream().map(l -> {
                EmployeeDto ed = employeeService.findById(l);
                return ed.getName();
            }).collect(Collectors.toList());
        }
        PollingDto pollingDto = new PollingDto();
        BeanUtils.copyProperties(polling, pollingDto);
        pollingDto.setWorkteamName(workteamNames);
        pollingDto.setRemindteamName(remindteamNames);
        pollingDto.setPersonNames(personNames);
        pollingDto.setReminderNames(reminderNames);
        List<SpotVo> vo = polling.getSpotTask();
        List<SpotTaskShow> re = new ArrayList<SpotTaskShow>();

        for(int i = 0; i< vo.size(); i++){
           List<SpotTaskShow> l = workTaskService.queryBySpotIdAndTaskIdIn(vo.get(i).getSpotId(),vo.get(i).getTaskId());
                   re.addAll(l);
        }
        pollingDto.setTaskDtos(re);
        return pollingDto;
    }

    @Override
    public Pagination<PollingDto> findByCondition(Specification<Polling> specification) {
        Page<Polling> all = pollingRepository.findAll(specification, JpaUtils.getPageRequest());
        List<PollingDto> dtos = all.getContent().stream().map(a -> {
            PollingDto dto = new PollingDto();
            if (a.getWorkteamId() != null) {
                List<String> teams = a.getWorkteamId().stream().map(w -> {
                    WorkTeamDto one = workTeamService.findOne(w);
                    return one.getTeamName();
                }).collect(Collectors.toList());
                dto.setWorkteamName(teams);
            } else if (a.getPersonIds() != null) {
                List<String> names = a.getPersonIds().stream().map(w -> {
                    EmployeeDto one = employeeService.findById(w);
                    return one.getName();
                }).collect(Collectors.toList());
                dto.setPersonNames(names);
            }
            BeanUtils.copyProperties(a, dto);
            return dto;
        }).collect(Collectors.toList());
        Pagination<PollingDto> page = new Pagination<>((int) all.getTotalElements(), dtos);
        return page;
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean update(Polling polling) throws SchedulerException {
        Polling poll = pollingRepository.findById(polling.getId()).orElse(null);
        if (poll == null) {
            throw new BusinessException(ErrorEnum.NO_POLLING);
        }
        this.deletePolling(poll.getId());
        Long pollingId = this.addPolling(polling);
        patrolRepository.updatePollingId(poll.getId(), pollingId);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean changeStatus(Long pollingId, Boolean activated) throws SchedulerException {
        pollingRepository.updateActivated(activated, pollingId);
        Polling byId = pollingRepository.findById(pollingId).orElse(null);

        //非周期的修改状态
        if (byId.getRegularType() == 0) {
            if (activated) {
                for (int i = 0; i < byId.getUnRegularTimes().size(); i++) {
                    try {
                        scheduler.resumeJob(new JobKey(CREATE_UNREGULAR_PATROL, byId.getId() + "_" + i));
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int i = 0; i < byId.getUnRegularTimes().size(); i++) {
                    try {
                        scheduler.pauseJob(new JobKey(CREATE_UNREGULAR_PATROL, byId.getId() + "_" + i));
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }

        Long id = byId.getId();
        JobKey key2 = new JobKey(CREATE_PATROL, id.toString());
        if (activated) {
            scheduler.resumeJob(key2);
        } else {
            scheduler.pauseJob(key2);

        }
        return true;
    }

    @Override
    public void repairAllTrigger() throws SchedulerException {
        List<String> trigger = pollingRepository.findAll().stream().map(k -> {
            return k.getJobGroupName();
        }).collect(Collectors.toList());
        trigger.forEach(s -> {
            TriggerKey k2 = new TriggerKey(CREATE_PATROL, s);
            //error状态
            try {
                Trigger.TriggerState ts = scheduler.getTriggerState(k2);
                if ("ERROR".equals(ts.toString())) {
                    scheduler.resetTriggerFromErrorState(k2);
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            }

        });
    }


}
