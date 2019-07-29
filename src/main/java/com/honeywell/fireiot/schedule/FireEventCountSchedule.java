package com.honeywell.fireiot.schedule;

import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.fire.service.FireEventService;
import com.honeywell.fireiot.repository.BusinessDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;

import java.util.List;

@Service
@EnableScheduling
public class FireEventCountSchedule {
    @Autowired
    BusinessDeviceRepository businessDeviceRepo;
    @Autowired
    FireEventService fireEventService;

    @Scheduled(cron = "0 0 1 1 * ? ")  //每月1号凌晨1点执行 批量更新上个月的事件统计数据
    public void  updateEventCount(){
       List<BusinessDevice> devices =  businessDeviceRepo.getDevice("消防报警系统");
        //获取前月的第一天
        Calendar cal_1=Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        cal_1.set(Calendar.HOUR_OF_DAY, 0);
        cal_1.set(Calendar.MINUTE, 0);
        cal_1.set(Calendar.SECOND, 0);


        //获取前月的最后一天
        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天
        cale.set(Calendar.HOUR_OF_DAY, 23);
        cale.set(Calendar.MINUTE, 59);
        cale.set(Calendar.SECOND, 59);
       for(BusinessDevice businessDevice :devices){

           fireEventService.updateEventCount(businessDevice.getDeviceNo(), cal_1.getTime().getTime(),cale.getTime().getTime());
       }
    }

}
