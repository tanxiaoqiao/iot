package com.honeywell.fireiot.job;


import com.honeywell.fireiot.dto.PushBody;
import com.honeywell.fireiot.dto.WorkTeamDto;
import com.honeywell.fireiot.entity.DailyMaintenance;
import com.honeywell.fireiot.entity.Maintenance;
import com.honeywell.fireiot.repository.DailyMaintenanceReporsitory;
import com.honeywell.fireiot.repository.MaintenanceReporsitory;
import com.honeywell.fireiot.service.EmployeeService;
import com.honeywell.fireiot.service.UserService;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.utils.HttpUtils;
import com.honeywell.fireiot.websocket.WebSocketServer;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Component
@EnableScheduling
public class SendWorkorderMsgJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendWorkorderMsgJob.class);

    @Autowired
    DailyMaintenanceReporsitory dailyMaintenanceReporsitory;

    @Autowired
    MaintenanceReporsitory maintenanceReporsitory;

    @Autowired
    WebSocketServer webSocketServer;

    @Autowired
    WorkTeamService workTeamService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    UserService userService;

    //推送消息
    @Transactional(rollbackOn = Exception.class)
    @Scheduled(cron = "0 0 7 * * ? ")
    public void executeJob() throws ParseException {
        String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        LOGGER.info("今日消息推送：{}", today);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse(today + " 00:00:00");
        Date end = sdf.parse(today + " 11:59:00");
        List<DailyMaintenance> lists = dailyMaintenanceReporsitory.findAllByStartTime(start, end);
        if (CollectionUtils.isEmpty(lists)) {
            return;
        }
        List<Long> ids = lists.stream().map(l -> l.getMaintenanceId()).collect(Collectors.toList());
        List<Maintenance> maintenances = maintenanceReporsitory.findAllById(ids);
        maintenances.forEach(m -> {
            WorkTeamDto one = workTeamService.findOne(m.getWorkteamId());

            if (!CollectionUtils.isEmpty(one.getAllIds())) {
                String[] strs = one.getAllIds().toArray(new String[one.getAllIds().size()]);
                String msg = "今日需要完成：" + m.getName();
                try {
                    PushBody rb = new PushBody("工单通知", "MaintenanceTopic", strs, msg, 0);
                    HttpUtils.sendPost(HttpUtils.getPushUrl(), rb);
                    rb.setType(1);
                    HttpUtils.sendPost(HttpUtils.getPushUrl(), rb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });


    }

}
