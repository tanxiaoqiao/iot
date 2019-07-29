package com.honeywell.fireiot.config;


import com.honeywell.fireiot.repository.PatrolRepository;
import com.honeywell.fireiot.repository.PatrolSpotInspectRepository;
import com.honeywell.fireiot.repository.PollingRepository;
import com.honeywell.fireiot.repository.WorkorderRepository;
import com.honeywell.fireiot.service.EmployeeService;
import com.honeywell.fireiot.service.ProcessService;
import com.honeywell.fireiot.service.UserService;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.websocket.WebSocketServer;
import org.quartz.Scheduler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (ApplicationContextProvider.applicationContext == null) {
            ApplicationContextProvider.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static ProcessService getProcessService() {
        ProcessService processService = null;
        processService = getApplicationContext().getBean(
                ProcessService.class);
        return processService;
    }


    public static PollingRepository getPollingRepository() {
        PollingRepository pollingRepository = null;
        pollingRepository = getApplicationContext().getBean(
                PollingRepository.class);
        return pollingRepository;
    }

    public static PatrolRepository getPatrolRepository() {
        PatrolRepository patrolRepository = null;
        patrolRepository = getApplicationContext().getBean(
                PatrolRepository.class);
        return patrolRepository;
    }

    public static WorkTeamService getWorkTeamService() {
        WorkTeamService workTeamService = null;
        workTeamService = getApplicationContext().getBean(
                WorkTeamService.class);
        return workTeamService;
    }

    public static WebSocketServer getWebSocketServer() {
        WebSocketServer webSocketServer = null;
        webSocketServer = getApplicationContext().getBean(
                WebSocketServer.class);
        return webSocketServer;
    }

    public static EmployeeService getEmployeeService() {
        EmployeeService employeeService = null;
        employeeService = getApplicationContext().getBean(
                EmployeeService.class);
        return employeeService;
    }


    public static UserService getUserService() {
        UserService userService = null;
        userService = getApplicationContext().getBean(
                UserService.class);
        return userService;
    }

    public static Scheduler getScheduler() {
        Scheduler scheduler = null;
        scheduler = getApplicationContext().getBean(
                Scheduler.class);
        return scheduler;
    }


    public static PatrolSpotInspectRepository getPatrolSpotInspectRepository() {
        PatrolSpotInspectRepository patrolSpotInspectRepository = null;
        patrolSpotInspectRepository = getApplicationContext().getBean(
                PatrolSpotInspectRepository.class);
        return patrolSpotInspectRepository;
    }

    public static WorkorderRepository getWorkorderRepository() {
        WorkorderRepository workorderRepository = null;
        workorderRepository = getApplicationContext().getBean(
                WorkorderRepository.class);
        return workorderRepository;
    }


}
