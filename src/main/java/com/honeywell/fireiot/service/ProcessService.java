package com.honeywell.fireiot.service;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.utils.SessionUtils;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Activiti服务处理
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/21
 */
@Component
@Transactional(readOnly = true)
public class ProcessService {

    final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    @Autowired
    ProcessEngine engine;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    IdentityService identityService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;


    @Transactional(readOnly = false)
    public String deployProcess(String processName) {
        RepositoryService service = engine.getRepositoryService();
        if (null == processName) {
            throw new BusinessException(ErrorEnum.PROCESS_NAME_NOT_NULL);
        }
        Deployment deployment = service.createDeployment().addClasspathResource("bpmn/" + processName).deploy();
        return deployment.getId();
    }


    @Transactional(readOnly = false)
    public String deployAndStartProcess(String processName, Map<String, Object> variables) {
        RepositoryService service = engine.getRepositoryService();
        HistoricTaskInstance task =
                historyService.createHistoricTaskInstanceQuery().taskAssignee(SessionUtils.getCurrentUser().getUsername()).singleResult();
        if (null == processName) {
            throw new BusinessException(ErrorEnum.PROCESS_NAME_NOT_NULL);
        }
        Deployment deployment = service.createDeployment().addClasspathResource("bpmn/" + processName).deploy();
        ProcessDefinitionQuery pdq = service.createProcessDefinitionQuery().deploymentId(deployment.getId());
        logger.info("部署启动的工作流模板：" + pdq.list().get(0).getId());

        ProcessInstance pi = engine.getRuntimeService().startProcessInstanceById(pdq.list().get(0).getId(),
                variables);// 使用流程定义ID开启一个流程
        logger.info("流程定义的ID：" + pi.getProcessDefinitionId());
        logger.info("流程实例的ID：" + pi.getId());
        return pi.getProcessDefinitionId();
    }

    /**
     * 启动一个工作流
     *
     * @param processName 工作流模板 （启动最新的版本）
     * @param variables   参数表
     * @return
     */
    @Transactional(readOnly = false)
    public String startProcess(String processName, Map<String, Object> variables) {
        //  identityService.setAuthenticatedUserId(SessionUtils.getCurrentUser().getUsername());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processName, variables);
        return processInstance.getId();
    }

    /**
     * 执行某个流程节点
     *
     * @param taskId
     * @param variables
     */
    @Transactional(readOnly = false)
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
        logger.info("完成任务：" + taskId);
    }

    @Transactional(readOnly = false)
    public void claim(String taskId, String username) {
        taskService.claim(taskId, username);
        logger.info("签收任务：" + taskId + " 签收人：" + username);
    }

    public Task findTaskByProcessInstanceId(String processInstanceId, String assignee) {
        return engine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).taskAssignee(assignee).active().singleResult();
    }


//	/**
//	 * 已办任务列表
//	 *
//	 * @param userId
//	 * @param pageable
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<HistoricTaskInstance> findDoneTask(String userId, Pageable pageable) {
//		List<HistoricTaskInstance> hpis = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId)
// .finished().orderByTaskCreateTime().desc()
//				.listPage(pageable.getOffset(), pageable.getPageSize());
//		Long count = historyService.createHistoricTaskInstanceQuery().finished().taskAssignee(userId).count();
//
//		BasePage<HistoricTaskInstance> p = new BasePage<HistoricTaskInstance>(pageable.getPageNumber(), pageable
// .getPageSize(), count, hpis,
//				pageable.getSort());
//		return p;
//	}
//
//	/**
//	 * 办结任务列表
//	 *
//	 * @param userId
//	 * @param pageable
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<HistoricTaskInstance> findFinishedTask(String userId, Pageable pageable) {
//		//查询当前用户的岗位
//		List<String> depGroupNoList = ugService.findDepGroupNoByUser(UserUtils.getCurrentUser());
//		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskInvolvedUser(userId)
// .list();
//		List<HistoricTaskInstance> hpis = historyService.createHistoricTaskInstanceQuery().taskCandidateGroupIn
// (depGroupNoList).orderByTaskCreateTime().desc()
//				.listPage(pageable.getOffset(), pageable.getPageSize());
//		Long count = historyService.createHistoricTaskInstanceQuery().taskCandidateGroupIn(depGroupNoList).finished()
// .count();
//
//		BasePage<HistoricTaskInstance> p = new BasePage<HistoricTaskInstance>(pageable.getPageNumber(), pageable
// .getPageSize(), count, hpis,
//				pageable.getSort());
//
//		return p;
//	}


    /**
     * 通过key启动流程
     *
     * @param classType   activity模板
     * @param businessKey 业务键
     * @param variables   流程变量
     * @return
     */
    public ProcessInstance startProcessInstanceByKey(String classType, String businessKey,
                                                     Map<String, Object> variables) {
        logger.info("设置启动人");
        identityService.setAuthenticatedUserId(SessionUtils.getCurrentUser().getUsername());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(classType, businessKey,
                variables);
        return processInstance;
    }

    /**
     * 根据流程定义id，变量完成任务
     *
     * @param processInstanceId
     * @param vars
     */
    public void completeByProcessAndVa(String processInstanceId, Map<String, Object> vars) {
        Task singleResult = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
        taskService.complete(singleResult.getId(), vars);
    }

    /**
     * 根据流程id和用户名查询任务，并完成(无变量)
     *
     * @param instanceId
     * @param username
     */
    public void completeByProcessAndAss(String instanceId, String username) {
        Task task =
                taskService.createTaskQuery().processInstanceId(instanceId).taskAssignee(username).active().singleResult();
        taskService.complete(task.getId());
    }

    public void completeByProcess(String instanceId, String username, Map<String, Object> vars) {
        Task task =
                taskService.createTaskQuery().processInstanceId(instanceId).taskAssignee(username).active().singleResult();
        taskService.complete(task.getId(), vars);
    }

    /**
     * 根据流程id查找未处理的task
     *
     * @param instanceId
     * @param action
     * @return
     */
    public List<Task> findActiveTaskByInstanceId(String instanceId, String action) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(instanceId).taskName(action).active().list();
        return list;
    }

    /**
     * 根据taskid完成任务
     *
     * @param id
     */
    public void completeTask(String id) {
        taskService.complete(id);
    }

    /**
     * 通过流程定义id计算条数
     *
     * @param instanceId
     * @return
     */
    public int countByInstanceIdActive(String instanceId) {
        long count = taskService.createTaskQuery().processInstanceId(instanceId).active().count();
        return new Long(count).intValue();
    }

    /**
     * 根据流程id和assignee查询未处理的task
     *
     * @param instanceId
     * @param username
     * @return
     */
    public Task findByProcessAndAss(String instanceId, String username) {
        Task task =
                taskService.createTaskQuery().processInstanceId(instanceId).taskAssignee(username).active().singleResult();
        return task;
    }

    /**
     * 根据businesskey得到流程定义Id
     *
     * @param key
     * @return
     */
    public String findInstanceIdByBusinessKey(String key) {
        Execution re = runtimeService.createExecutionQuery().processInstanceBusinessKey(key).singleResult();
        String processInstanceId = re.getProcessInstanceId();
        return processInstanceId;
    }

    /**
     * 通过流程定义id获取启动人
     *
     * @param processInstanceId
     * @return
     */
    public String getStartUserID(String processInstanceId) {
        HistoricProcessInstance singleResult =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
                        .singleResult();
        return singleResult.getStartUserId();
    }

    public String findDocuByTaskId(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).active().singleResult().getDescription();
    }

    /**
     * 通过流程定义id找到buinesskey所对应的类名和实例id
     *
     * @param processInstanceId
     * @return
     */
    public String[] findKeyByProcessInstanceId(String processInstanceId) {
        ProcessInstance singleResult =
                runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String businessKey = singleResult.getBusinessKey();
        if (businessKey == null) {
            return null;
        }
        String[] split = businessKey.split("\\.");
        return split;
    }

    public String[] findKeyByTaskId(String taskId) {
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        return findKeyByProcessInstanceId(processInstanceId);
    }

    public String findProKeyByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).active().singleResult();
        ProcessDefinition processDefinition = engine.getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId()).singleResult();
        return processDefinition.getKey();
    }

    public int findVarByTaskId(String taskId, String variableName) {
        Task task = taskService.createTaskQuery().taskId(taskId).active().singleResult();
        HistoricVariableInstance result = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).variableName(variableName).singleResult();
        return (Integer) result.getValue();
    }

    public String findInstanceIdByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).active().singleResult();
        return task.getProcessInstanceId();
    }

    public String findNameByTaskId(String taskId) {
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return task.getName();
    }

    public int countByTaskIdActive(String taskId) {
        long count = taskService.createTaskQuery().taskId(taskId).active().count();
        return new Long(count).intValue();
    }

    public int countHuiqian(String processInstanceId, String taskName) {
        long count =
                taskService.createTaskQuery().processInstanceId(processInstanceId).taskName(taskName).active().count();
        return new Long(count).intValue();
    }

    public List<Task> findActiveTaskByInstanceId(String processInstanceId) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        return list;
    }

    public Task findTaskById(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**
     * 根据任务id获得岗位id（只获得第一个结果）
     *
     * @param taskId
     * @return
     */
    public String findGroupIdByTaskId(String taskId) {
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(taskId);
        if (identityLinksForTask.size() > 0) {
            return identityLinksForTask.get(0).getGroupId();
        }
        return null;
    }

    /**
     * 转办
     *
     * @param taskId
     * @param username
     */
    @Transactional(readOnly = false)
    public void changeAssignee(String taskId, String username) {
        Task task = taskService.createTaskQuery().taskId(taskId).active().singleResult();
        task.setAssignee(username);
        taskService.saveTask(task);
    }

    /**
     * 根据task获取流程定义
     *
     * @param taskInstance
     * @return
     */
    public ProcessDefinition findProcdefByTask(HistoricTaskInstance taskInstance) {
        return engine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(taskInstance.getProcessDefinitionId()).singleResult();
    }

    /**
     * 挂起流程
     *
     * @param processInstanceId
     */
    public void suspendProcessInstanceById(String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 查找任务历史
     *
     * @param taskId
     * @return
     */
    public HistoricTaskInstance findHistoryTask(String taskId) {
        return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
    }

    /**
     * 根据任务查找businessKey
     *
     * @param taskId
     * @return businessKey
     */
    public String findBusinessKeyByTask(String taskId) {
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        HistoricProcessInstance processInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return processInstance.getBusinessKey();
    }

    /**
     * 删除运行中的流程实例
     *
     * @param instanceId
     */
    @Transactional(readOnly = false)
    public void deleteRunProcess(String instanceId) {
        runtimeService.deleteProcessInstance(instanceId, null);
    }


}
