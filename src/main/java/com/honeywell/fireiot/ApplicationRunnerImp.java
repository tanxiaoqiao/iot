package com.honeywell.fireiot;

import com.honeywell.fireiot.fire.MTProtocolParse;
import com.honeywell.fireiot.service.BusinessDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: Kris
 * @Description:
 * @Date: 6/9/2018 11:54 AM
 */
@Component
public class ApplicationRunnerImp implements ApplicationRunner {


    @Autowired
    MTProtocolParse mtProtocolParse;

    @Autowired
    BusinessDeviceService businessDeviceService;

    @Value("${sysytem.statistics.month}")
    private boolean stats30Data;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        mtProtocolParse.initFireSystem();

        if(stats30Data){
            businessDeviceService.initSystem();
        }
    }
}

