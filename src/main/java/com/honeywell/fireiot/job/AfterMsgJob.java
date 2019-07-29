package com.honeywell.fireiot.job;


import com.honeywell.fireiot.config.ApplicationContextProvider;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.PushBody;
import com.honeywell.fireiot.dto.WorkTeamDto;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.entity.Polling;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.PatrolRepository;
import com.honeywell.fireiot.repository.PollingRepository;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.utils.HttpUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;


/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Component
@DisallowConcurrentExecution
public class AfterMsgJob implements Job {


    private static final Logger LOGGER = LoggerFactory.getLogger(AfterMsgJob.class);

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String patrolId = context.getJobDetail().getKey().getGroup();
        WorkTeamService workTeamService = ApplicationContextProvider.getWorkTeamService();
        PatrolRepository patrolRepository = ApplicationContextProvider.getPatrolRepository();
        Patrol patrol = patrolRepository.findById(Long.valueOf(patrolId)).orElse(null);
        LOGGER.info("推送任务快结束通知patrolId：{}", patrol.getId());
        if ( patrol.getStatus() == 2) {
        }
        String msg = patrol.getName() + ":" + " 任务即将到结束时间，请尽快完成";
        if (!CollectionUtils.isEmpty(patrol.getWorkTeamIds())) {
            patrol.getWorkTeamIds().forEach(s -> {
                WorkTeamDto one = workTeamService.findOne(s);
                try {
                    String[] strs = one.getAllIds().toArray(new String[one.getAllIds().size()]);
                    sendMsg(strs, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
        if (!CollectionUtils.isEmpty(patrol.getPersonIds())) {
            try {
                String[] userIds = patrol.getPersonIds().toArray(new String[patrol.getPersonIds().size()]);
                sendMsg(userIds, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private void sendMsg(String[] strs, String msg) {
        PushBody rb = new PushBody("巡检通知", "PollingTopic", strs, msg, 0);
        HttpUtils.sendPost(HttpUtils.getPushUrl(), rb);
        rb.setType(1);
        HttpUtils.sendPost(HttpUtils.getPushUrl(), rb);
    }


}
