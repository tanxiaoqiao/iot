package com.honeywell.fireiot.job;


import com.honeywell.fireiot.config.ApplicationContextProvider;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.PatrolRepository;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Component
@DisallowConcurrentExecution
public class CheckJob implements Job {


    private static final Logger LOGGER = LoggerFactory.getLogger(CheckJob.class);


    @Override
    @Transactional(rollbackOn = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String group = context.getJobDetail().getKey().getGroup();
        Long patrolId = Long.valueOf(group);
        PatrolRepository patrolRepository = ApplicationContextProvider.getPatrolRepository();
        Patrol patrol = patrolRepository.findById(patrolId).orElse(null);
        LOGGER.info("检查任务是否完成patrolId:{}", patrol.getId());
        if (patrol == null) {
            throw new BusinessException(ErrorEnum.NO_POTRAL);
        }
        int inspected = patrol.getNormalNums() + patrol.getExceptionNums();
        int i = patrol.getSpotCount() - inspected - patrol.getSupplementNums();
        patrol.setMissNums(i);
        if (i > 0) {
            //未完成
            patrol.setStatus(4);
        }
        patrolRepository.save(patrol);
    }

}
