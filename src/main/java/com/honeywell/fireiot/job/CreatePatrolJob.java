package com.honeywell.fireiot.job;


import com.honeywell.fireiot.config.ApplicationContextProvider;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.WorkTeamDto;
import com.honeywell.fireiot.entity.EmpPatrol;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.entity.PatrolSpotInspect;
import com.honeywell.fireiot.entity.Polling;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.PatrolRepository;
import com.honeywell.fireiot.repository.PatrolSpotInspectRepository;
import com.honeywell.fireiot.repository.PollingRepository;
import com.honeywell.fireiot.service.WorkTeamService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;
import org.quartz.impl.calendar.AnnualCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author: create by kris
 * @description: 每天的定时任务 生成patrol
 * @date:1/28/2019
 */
@Component
@DisallowConcurrentExecution
public class CreatePatrolJob implements Job {


    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePatrolJob.class);
    private static String END_JOB = "endJob";
    private static String PRE_REMIND = "preRemind";
    private static String AFTER_REMIND = "afterRemind";

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String group = context.getJobDetail().getKey().getGroup();
        PollingRepository pollingRepository = ApplicationContextProvider.getPollingRepository();
        PatrolRepository patrolRepository = ApplicationContextProvider.getPatrolRepository();
        WorkTeamService workTeamService = ApplicationContextProvider.getWorkTeamService();
        Scheduler scheduler = ApplicationContextProvider.getScheduler();
        PatrolSpotInspectRepository patrolSpotInspectRepository = ApplicationContextProvider.getPatrolSpotInspectRepository();
        Polling poll = pollingRepository.findById(Long.valueOf(group)).orElse(null);
//        //额外节假日不生成
//        //休息日不生成
//        boolean shouldWork = true;
//        //额外节假日不生成
//        ArrayList<Timestamp> holiday = poll.getExtraHoliday();
//        String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
//        if (!CollectionUtils.isEmpty(holiday)) {
//            for (int i = 0; i < holiday.size(); i++) {
//                String ho = DateFormatUtils.format(holiday.get(i), "yyyy-MM-dd");
//                LOGGER.info("额外节假日：{}", ho);
//                if (today.equals(ho)) {
//                    shouldWork = false;
//                    return;
//                }
//            }
//        }
//        //无需工作
//        if (shouldWork = false) {
//            return;
//        }
        //如果使用节假日
        java.util.Calendar instance = java.util.Calendar.getInstance();
        instance.setTime(new Date());
        if (poll.getUseHoliday()) {
            int day = instance.get(java.util.Calendar.DAY_OF_WEEK);
            org.quartz.impl.calendar.AnnualCalendar quartzCalendar = null;
            try {
                quartzCalendar = (AnnualCalendar) scheduler.getCalendar("DEFINE_HOLIDAY");
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
            if (quartzCalendar != null) {
                while (quartzCalendar.isDayExcluded(instance) || day == java.util.Calendar.SATURDAY || day == java.util.Calendar.SUNDAY) {
                    instance.add(Calendar.DAY_OF_YEAR, 1);
                }
            }
        }
        Patrol patrol = new Patrol();
        BeanUtils.copyProperties(poll, patrol);
        patrol.setId(null);
        patrol.setPollingId(poll.getId());
        String format = DateFormatUtils.format(instance.getTime(), "yyyy-MM-dd");
        patrol.setPreStartTime(Timestamp.valueOf(format+ " " + poll.getPreMinute()));
        patrol.setPreEndTime(Timestamp.valueOf(format + " " + poll.getEndMinute()));

        // 关联emppatrol表
        if (poll.getWorkteamId() != null) {
            List<EmpPatrol> collect = new ArrayList<>();
            poll.getWorkteamId().forEach(w -> {
                WorkTeamDto one = workTeamService.findOne(w);
                one.getAllIds().forEach(m -> {
                    EmpPatrol ep = new EmpPatrol();
                    ep.setEmployeeId(m);
                    collect.add(ep);
                });
            });
            patrol.setEmpPatrols(collect);
        }
        if (poll.getPersonIds() != null) {
            List<EmpPatrol> collect = poll.getPersonIds().stream().map(p -> {
                EmpPatrol ep = new EmpPatrol();
                ep.setEmployeeId(p);
                return ep;
            }).collect(Collectors.toList());
            patrol.setEmpPatrols(collect);
        }
        Patrol save = patrolRepository.save(patrol);
        //提供给spot
        if (poll.getSpotTask()==null) {
            throw new BusinessException(ErrorEnum.SPOT_NOT_EXIST);
        }
            List<PatrolSpotInspect> psi = save.getSpotTask().stream().map(s -> {
                PatrolSpotInspect pai = new PatrolSpotInspect();
                pai.setPatrolId(save.getId());
                pai.setSpotId(s.getSpotId());
                return pai;
            }).collect(Collectors.toList());
            patrolSpotInspectRepository.saveAll(psi);

        LOGGER.info("patrol成功创建 patrolID:{}", patrol.getId());
        //生成2个job任务
        JobKey endkey = new JobKey(END_JOB, save.getId().toString());
        JobDetail endJob =
                JobBuilder.newJob(CheckJob.class).withIdentity(endkey).build();
        TriggerKey endKey = TriggerKey.triggerKey(END_JOB, save.getId().toString());

        //按新的cronExpression表达式构建一个新的trigger
        TriggerBuilder<Trigger> te = newTrigger().withIdentity(endKey);

        SimpleTrigger startt = null;
        SimpleTrigger endt = null;
        //周期

        String str = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String pre = str + " " + poll.getPreMinute();
        //设置jobstart
        Timestamp pret = Timestamp.valueOf(pre);

        LOGGER.info("巡检开始时间:{}", pre);
        //设置jobend
        String aft = str + " " + poll.getEndMinute();
        Timestamp aftt = Timestamp.valueOf(aft);
        endt = (SimpleTrigger) te.startAt(aftt).build();
        LOGGER.info("巡检结束时间:{}", aft);

        try {
            scheduler.start();
            scheduler.scheduleJob(endJob, endt);
            //构建消息
            getDailyJob(save);
            LOGGER.info("巡检任务时间创建成功");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


    public void getDailyJob(Patrol save) {
        LOGGER.info("创建每日消息提醒");
        //根据polling找到trigger
        PollingRepository pollingRepository = ApplicationContextProvider.getPollingRepository();
        Polling a = pollingRepository.findById(save.getPollingId()).orElse(null);
        if (a == null) {
            throw new BusinessException(ErrorEnum.NO_POLLING);
        }
        JobKey preJob = new JobKey(PRE_REMIND, save.getId().toString());
        JobKey aftJob = new JobKey(AFTER_REMIND, save.getId().toString());


        JobDetail sendMsgJob =
                JobBuilder.newJob(SendMsgJob.class).withIdentity(preJob).build();
        JobDetail afterMsgJob =
                JobBuilder.newJob(AfterMsgJob.class).withIdentity(aftJob).build();

        TriggerKey preKey = TriggerKey.triggerKey(PRE_REMIND, save.getId().toString());
        TriggerKey aftKey = TriggerKey.triggerKey(AFTER_REMIND, save.getId().toString());

        SimpleTrigger pretigger = null;
        SimpleTrigger aftertigger = null;

        TriggerBuilder<Trigger> sttbpre = newTrigger().withIdentity(preKey);
        TriggerBuilder<Trigger> sttbaft = newTrigger().withIdentity(aftKey);

        String str = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String pre = str + " " + a.getPreMinute();
        //设置推送时间
        long st = Timestamp.valueOf(pre).getTime() - a.getPreRemind() * 60000;
        pretigger = (SimpleTrigger) sttbpre.startAt(new Date(st)).build();
        LOGGER.info("pollingID:{} 提前通知时间:{}", a.getId(), DateFormatUtils.format(new Date(st), "yyyy-MM-dd " +
                "HH:mm:ss"));
        //结束提醒
        if (a.getEndRemind() != null) {
            String aft = str + " " + a.getEndMinute();
            long af = Timestamp.valueOf(aft).getTime() - a.getEndRemind() * 60000;
            aftertigger = (SimpleTrigger) sttbaft.startAt(new Date(af)).build();
            LOGGER.info("pollingID:{} 快结束通知时间:{}", a.getId(), DateFormatUtils.format(new Date(st), "yyyy-MM-dd " +
                    "HH:mm:ss"));
        }

        Scheduler scheduler = ApplicationContextProvider.getScheduler();
        try {
            scheduler.start();
            scheduler.scheduleJob(sendMsgJob, pretigger);
            scheduler.scheduleJob(afterMsgJob, aftertigger);
            LOGGER.info("消息提醒构建成功");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
}
