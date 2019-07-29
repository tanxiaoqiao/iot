package com.honeywell.fireiot.job;

import com.honeywell.fireiot.config.ApplicationContextProvider;
import com.honeywell.fireiot.constant.StatusEnum;
import com.honeywell.fireiot.dto.PushBody;
import com.honeywell.fireiot.dto.WorkTeamDto;
import com.honeywell.fireiot.entity.Workorder;
import com.honeywell.fireiot.repository.WorkorderRepository;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


/**
 * @Author: Kris
 * @Date: 2019-06-17  13:07
 */

@Component
@DisallowConcurrentExecution
@Slf4j
@EnableScheduling
public class WorkorderMsgEndJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("工单消息推送=======");
        String workorderId = context.getJobDetail().getKey().getGroup();
        String description = context.getJobDetail().getDescription();
        WorkorderRepository workorderRepository = ApplicationContextProvider.getWorkorderRepository();
        Workorder workorder = workorderRepository.findById(Long.valueOf(workorderId)).orElse(null);
        WorkTeamService workTeamService = ApplicationContextProvider.getWorkTeamService();
        //已经完成的也不通知
        if (workorder == null || StatusEnum.TERMINATE == workorder.getStatus() || StatusEnum.FINAL == workorder.getStatus()) {
            return;
        }

        WorkTeamDto one = workTeamService.findOne(workorder.getWorkTeamId());
        if (CollectionUtils.isNotEmpty(one.getAllIds())) {
            String[] strs = one.getAllIds().toArray(new String[one.getAllIds().size()]);
            String msg = "工单号：" + workorderId + " 工单名：" + workorder.getTitle() + " " + description;
            log.info(msg);
            try {
                PushBody rb = new PushBody("工单通知", "MaintenanceTopic", strs, msg, 0);
                HttpUtils.sendPost(HttpUtils.getPushUrl(), rb);
                rb.setType(1);
                HttpUtils.sendPost(HttpUtils.getPushUrl(), rb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
