package com.honeywell.fireiot.listener;



import com.honeywell.fireiot.config.ApplicationContextProvider;
import com.honeywell.fireiot.constant.StatusEnum;
import com.honeywell.fireiot.entity.Workorder;
import com.honeywell.fireiot.repository.WorkorderRepository;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;
/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Component
public class AcceptListener implements TaskListener {


    @Override
    public void notify(DelegateTask delegateTask) {
        WorkorderRepository workorderRepository = ApplicationContextProvider.getWorkorderRepository();
        String pid = delegateTask.getProcessInstanceId();
        Workorder wo = workorderRepository.findByProcessId(pid);
        wo.setStatus(StatusEnum.ACCEPT);
        workorderRepository.save(wo);
        delegateTask.addCandidateGroup(wo.getWorkTeamId());


    }


}