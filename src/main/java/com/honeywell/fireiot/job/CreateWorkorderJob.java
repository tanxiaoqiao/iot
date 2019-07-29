package com.honeywell.fireiot.job;


import com.honeywell.fireiot.entity.DailyMaintenance;
import com.honeywell.fireiot.entity.Maintenance;
import com.honeywell.fireiot.repository.DailyMaintenanceReporsitory;
import com.honeywell.fireiot.repository.MaintenanceReporsitory;
import com.honeywell.fireiot.service.WorkorderService;
import com.honeywell.fireiot.vo.SaveWorkorderVo;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Component
@EnableScheduling
public class CreateWorkorderJob {


    private static final Logger LOGGER = LoggerFactory.getLogger(CreateWorkorderJob.class);

    @Autowired
    DailyMaintenanceReporsitory dailyMaintenanceReporsitory;

    @Autowired
    MaintenanceReporsitory maintenanceReporsitory;

    @Autowired
    WorkorderService workorderService;

    //生成工单
    @Transactional(rollbackOn = Exception.class)
    @Scheduled(cron = "0 15 12 * * ? ")
    public void executeJob() {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        today.add(Calendar.DATE, 30);
        //查询30天内自动生成工单
        List<DailyMaintenance> list = dailyMaintenanceReporsitory.findBetweenStartTime(new Date(), today.getTime());
        today.add(Calendar.DATE, -30);
        list.forEach(l -> {
            Calendar future = Calendar.getInstance();
            future.setTime(l.getStartTime());
            //提前时间
            future.add(Calendar.DATE, -l.getDays());
            //同一天，生成工单
            if (today.get(1) == future.get(1) && today.get(6) == future.get(6) && l.getIsAuto()) {
                LOGGER.info("maintenanceId:{}",l.getMaintenanceId());
                Maintenance m = maintenanceReporsitory.findById(l.getMaintenanceId()).orElse(null);
                SaveWorkorderVo workorder = new SaveWorkorderVo();
                workorder.setPreStartTime(l.getStartTime());
                workorder.setPreEndTime(new Timestamp(l.getStartTime().getTime() + 24 * 60 * 1000 * l.getWorkDays()));
                LOGGER.info(m.getName()+"=====");
                workorder.setTitle(m.getName());
                workorder.setDailyMaintenanceId(l.getId());
                workorder.setWorkTeamId(m.getWorkteamId());
                workorder.setWorkTeamName(m.getWorkteamName());
                workorder.setSteps(m.getSteps());
                workorder.setDeviceIds(m.getDeviceIds());
                //维保工单
                workorder.setType(1);
                workorder.setSaveAuto(m.getSaveAuto());
                workorder.setLocationIds(m.getLocationIds());
                String format = DateFormatUtils.format(m.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                workorder.setPreStartTime(Timestamp.valueOf(format));
                //工作时长
                long endTime = m.getStartTime().getTime() + m.getWorkDays() * 24 * 60 * 60000;
                String preEnd = DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss");
                workorder.setPreEndTime(Timestamp.valueOf(preEnd));
                try {
                    Long workorderId = workorderService.addWorkorder(workorder, null);
                    l.setWorkorderId(workorderId);
                    dailyMaintenanceReporsitory.save(l);
                    LOGGER.info("success!");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

}
