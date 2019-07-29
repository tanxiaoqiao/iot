package com.honeywell.fireiot.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.StatusEnum;
import com.honeywell.fireiot.constant.TaskNameEnum;
import com.honeywell.fireiot.dao.WorkorderDao;
import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.job.WorkorderMsgEndJob;
import com.honeywell.fireiot.repository.*;
import com.honeywell.fireiot.service.*;
import com.honeywell.fireiot.utils.HttpUtils;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.vo.SaveWorkorderVo;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Service
public class WorkorderServiceImpl implements WorkorderService {

    private static String WORKORDER_MSG_END_JOB = "workorderMsgEndJob";
    private static String WORKORDER_MSG_START_JOB = "workorderMsgStartJob";
    private static String START_MSG = "已经开始，请在规定时间内完成";
    private static String END_MSG = "已经到结束时间，请尽快完成";


    @Autowired
    WorkorderRepository workorderRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    HistoryOperationRepository historyOperationRepository;

    @Autowired
    WorkorderDao workorderDao;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    ProcessService processService;

    @Autowired
    BusinessDeviceService businessDeviceService;

    @Autowired
    LocationService locationService;

    @Autowired
    private Scheduler scheduler;

    @Value("${workorder.path}")
    private String remotePath;

    @Autowired
    LocationWorkorderRepository locationWorkorderRepository;


    @Autowired
    WorkorderEmpRepository workorderEmpRepository;

    private static ThreadFactory workOrderThreadFactory =
            new ThreadFactoryBuilder()
                    .setNameFormat("work-order-pool-%d").build();
    ExecutorService singleThreadPool =
            new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024), workOrderThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void deploy() {
        processService.deployProcess("workorder02.bpmn");

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Long addWorkorder(SaveWorkorderVo saveWorkorderVo, Long userId) {
        //设置发起人
        String pid = processService.startProcess("workorder02", null);
        Task task =
                taskService.createTaskQuery().processInstanceId(pid).taskName(TaskNameEnum.MAINTAIN).singleResult();
        taskService.addCandidateGroup(task.getId(), saveWorkorderVo.getWorkTeamId());
        if (task == null) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        //维保计划没有creator
        Workorder wo = new Workorder();
        BeanUtils.copyProperties(saveWorkorderVo, wo);
        wo.setDeviceIds(saveWorkorderVo.getDeviceIds());
        wo.setLocationIds(saveWorkorderVo.getLocationIds());
        wo.setSteps(saveWorkorderVo.getSteps());
        wo.setProcessId(pid);
        //建立报表查询表
        if (!CollectionUtils.isEmpty(saveWorkorderVo.getDeviceIds())) {
            List<WorkorderDeviceRel> drels = saveWorkorderVo.getDeviceIds().stream().map(l -> {
                return new WorkorderDeviceRel(l, wo);
            }).collect(Collectors.toList());
            wo.setWorkorderDeviceRels(drels);
        }
        if (!CollectionUtils.isEmpty(saveWorkorderVo.getLocationIds())) {
            List<WorkorderLocationRel> lrels = saveWorkorderVo.getLocationIds().stream().map(k -> {
                return new WorkorderLocationRel(k, wo);
            }).collect(Collectors.toList());
            wo.setWorkorderLocationRels(lrels);
        }
        if (saveWorkorderVo.getType() == 1) {
            wo.setCreatorName("维保自动生成");
        } else {
            EmployeeDto byId = employeeService.findByUserId(saveWorkorderVo.getCreator());
            wo.setCreatorName(byId.getName());
            wo.setMobile(byId.getTelephone());
            wo.setType(0);
        }
        if (saveWorkorderVo.getType() != 2) {
            processService.completeTask(task.getId());
        } else {
            wo.setAuditStatus(StatusEnum.APPLAY_AUDIT);
            wo.setStatus(StatusEnum.unConfirm);
        }
        Workorder save = workorderRepository.save(wo);
        HistoryOperation ho = new HistoryOperation(StatusEnum.ARRANGE, wo.getCreatorName(), save, null);
        historyOperationRepository.save(ho);
        //构建推送
        if (saveWorkorderVo.getPreStartTime() != null && saveWorkorderVo.getPreEndTime() != null) {
            sendMsg(wo.getId().toString(), wo.getPreEndTime(), WORKORDER_MSG_END_JOB, END_MSG);
            sendMsg(wo.getId().toString(), wo.getPreStartTime(), WORKORDER_MSG_START_JOB, START_MSG);
        }
        return save.getId();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean audit(Long workorderId, String accept, Long userId, String reason) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        if (workorder == null) {
            throw new BusinessException(ErrorEnum.NO_WORKORDER);
        }
        EmployeeDto emp = employeeService.findByUserId(userId.toString());
        if (!workorder.getAuditId().equalsIgnoreCase( emp.getId())) {
            throw new BusinessException(ErrorEnum.PERMISSION_NOT_EMPTY);
        }
        Task task =
                taskService.createTaskQuery().processInstanceId(workorder.getProcessId()).singleResult();
        if (task == null||TaskNameEnum.MAINTAIN .equalsIgnoreCase(task.getName())&& TaskNameEnum.AUDIT.equalsIgnoreCase(task.getName())) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        workorder.setAuditStatus(StatusEnum.AUDITED);
        workorderRepository.save(workorder);
        String info = "理由：" + reason;
        HistoryOperation ho = new HistoryOperation(StatusEnum.AUDITED, emp.getName(), workorder, info);
        historyOperationRepository.save(ho);
        //通知创建人
        if (workorder.getCreator() == null) {
            return true;
        }
        String msg = "工单号:" + workorderId + "已经审批";
        try {
            singleThreadPool.execute(() -> {
                PushBody rb = new PushBody("工单通知", "MaintenanceTopic", new String[]{workorder.getCreator()}, msg, 0);
                HttpUtils.sendPost(HttpUtils.getPushUrl(), rb);
            });
            singleThreadPool.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果是维保不通过
        if (TaskNameEnum.MAINTAIN.equalsIgnoreCase(task.getName()) && "no".equalsIgnoreCase(accept)) {
            return true;
        }
        Map<String, Object> varibles = new HashMap<>(8);
        //统一 no
        varibles.put("audit", accept);
        //设置历史记录
        processService.completeTask(task.getId(), varibles);
        return true;
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean applyAudit(Long workorderId, String apply, Long userId, String auditor) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        if (workorder == null) {
            throw new BusinessException(ErrorEnum.NO_WORKORDER);
        }
        if (workorder.getStatus() != null && StatusEnum.APPLAY_AUDIT == workorder.getStatus()) {
            throw new BusinessException(ErrorEnum.APPLIED);
        }
        Task task =
                taskService.createTaskQuery().processInstanceId(workorder.getProcessId()).singleResult();
        if (task == null) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        Map<String, Object> map = new HashMap<>(8);
        map.put("audit", "true");
        processService.completeTask(task.getId(), map);
        EmployeeDto ed = employeeService.findById(auditor);
        workorder.setAuditId(ed.getId());
        workorder.setAuditName(ed.getName());
        EmployeeDto user = employeeService.findByUserId(userId.toString());
        workorder.setAuditStatus(StatusEnum.APPLAY_AUDIT);
        Workorder save = workorderRepository.save(workorder);
        String info = "申请理由：" + apply;
        String msg = "工单号：" + workorder.getId() + "需要审批";
        try {
            singleThreadPool.execute(() -> {
                PushBody rb = new PushBody("工单通知", "MaintenanceTopic", new String[]{auditor}, msg, 0);
                HttpUtils.sendPost(HttpUtils.getPushUrl(), rb);
            });
            singleThreadPool.shutdown();
        } catch (Exception e) {
            e.printStackTrace();

        }
        HistoryOperation ho = new HistoryOperation(StatusEnum.APPLAY_AUDIT,
                user.getName(), save, info);
        historyOperationRepository.save(ho);
        return true;
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean arrange(Long workorderId, List<String> acceptors, Long userId) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        if (workorder == null) {
            throw new BusinessException(ErrorEnum.NO_WORKORDER);
        }
        Task task =
                taskService.createTaskQuery().processInstanceId(workorder.getProcessId()).taskName(TaskNameEnum.ARRANGE_TASK).singleResult();
        if (task == null) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        Map<String, Object> map = new HashMap<>(8);
        //设置接单候选人
        map.put("userList", acceptors);
        map.put("audit", "false");
        processService.completeTask(task.getId(), map);
        List<String> names = acceptors.stream().map(a -> {
            return employeeService.findById(a).getName();
        }).collect(Collectors.toList());
        String joins = StringUtils.join(names, ",");
        String info = "可选接单人" + joins;
        EmployeeDto ed = employeeService.findByUserId(userId.toString());
        //添加历史记录
        HistoryOperation ho = new HistoryOperation(StatusEnum.ACCEPT, ed.getName(), workorder, info);
        historyOperationRepository.save(ho);
        return true;
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean accept(Long workorderId, Long userId) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        if (workorder == null) {
            throw new BusinessException(ErrorEnum.NO_WORKORDER);
        }
        Task task =
                taskService.createTaskQuery().processInstanceId(workorder.getProcessId()).taskName(TaskNameEnum.ACCEPT_TASK).singleResult();
        if (task == null) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        EmployeeDto ed = employeeService.findByUserId(userId.toString());
        workorder.setActAcceptor(ed.getId());
        workorder.setActAcceptorName(ed.getName());
        workorderRepository.save(workorder);
        HistoryOperation ho = new HistoryOperation(StatusEnum.COMPLETE,
                ed.getName(), workorder, null);
        historyOperationRepository.save(ho);
        Map<String, Object> map = new HashMap<>(8);
        map.put("audit", "false");
        processService.completeTask(task.getId(), map);
        return true;
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean complete(Long workorderId, Long userId, List<Step> steps, String attachUrl) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        if (workorder == null) {
            throw new BusinessException(ErrorEnum.NO_WORKORDER);
        }
        Task task =
                taskService.createTaskQuery().processInstanceId(workorder.getProcessId()).taskName(TaskNameEnum.COMPLETE_TASK).singleResult();
        if (task == null) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        EmployeeDto ed = employeeService.findByUserId(userId.toString());
        HistoryOperation ho = new HistoryOperation(StatusEnum.TRACE,
                ed.getName(), workorder, null);
        //如果是维保工单
        workorder.setSteps((ArrayList<Step>) steps);
        workorder.setAttachUrl(attachUrl);
        workorderRepository.save(workorder);
        historyOperationRepository.save(ho);
        Map<String, Object> map = new HashMap<>(8);
        map.put("audit", "false");
        processService.completeTask(task.getId(), map);
        if (workorder.getSaveAuto() != null && workorder.getSaveAuto()) {
            trace(workorderId, "自动存档", userId);
        }
        return true;
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean refuse(Long workorderId, Long userId, String reason) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        if (workorder == null) {
            throw new BusinessException(ErrorEnum.NO_WORKORDER);
        }
        workorder.setActAcceptor(null);
        workorder.setActAcceptorName(null);
        workorderRepository.save(workorder);
        Task task =
                taskService.createTaskQuery().processInstanceId(workorder.getProcessId()).singleResult();
        if (task == null) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        EmployeeDto ed = employeeService.findByUserId(userId.toString());
        String info = "拒单理由 " + reason;
        Map<String, Object> map = new HashMap<>(8);
        map.put("dowork", "false");
        processService.completeTask(task.getId(), map);
        HistoryOperation ho = new HistoryOperation(StatusEnum.REFUSE,
                ed.getName(), workorder, info);
        historyOperationRepository.save(ho);
        return true;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean terminate(Long workorderId, String termiate, Long userId) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        if (workorder == null) {
            throw new BusinessException(ErrorEnum.NO_WORKORDER);
        }
        Task task =
                taskService.createTaskQuery().processInstanceId(workorder.getProcessId()).singleResult();
        if (task == null) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        HashMap<String, Object> map = new HashMap<>(8);
        map.put("audit", "neither");
        processService.completeTask(task.getId(), map);
        EmployeeDto ed = employeeService.findByUserId(userId.toString());
        workorder.setStatus(StatusEnum.TERMINATE);
        workorderRepository.save(workorder);
        HistoryOperation ho = new HistoryOperation(StatusEnum.TERMINATE, ed.getName(),
                workorder, termiate);
        historyOperationRepository.save(ho);
        return true;
    }

    @Override
    public Boolean updateWorkorder(Workorder workorder, Long userId) {
        //建立报表查询表
        if (!CollectionUtils.isEmpty(workorder.getDeviceIds())) {
            List<WorkorderDeviceRel> drels = workorder.getDeviceIds().stream().map(l -> {
                return new WorkorderDeviceRel(l, workorder);
            }).collect(Collectors.toList());
            workorder.setWorkorderDeviceRels(drels);
        }
        if (!CollectionUtils.isEmpty(workorder.getLocationIds())) {
            List<WorkorderLocationRel> lrels = workorder.getLocationIds().stream().map(k -> {
                return new WorkorderLocationRel(k, workorder);
            }).collect(Collectors.toList());
            workorder.setWorkorderLocationRels(lrels);
        }
        Workorder wo = workorderRepository.save(workorder);
        ProcessInstance pi =
                runtimeService.createProcessInstanceQuery().processInstanceId(wo.getProcessId()).singleResult();
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        taskService.setVariable(task.getId(), "userList", workorder.getAcceptorIds());
        EmployeeDto ed = employeeService.findByUserId(userId.toString());
        HistoryOperation ho = new HistoryOperation(StatusEnum.UPDATE, ed.getName(),
                workorder, null);
        historyOperationRepository.save(ho);
        return true;
    }

    @Override
    public WorkorderDto findWorkorderById(Long workorderId) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        List<HistoryOperation> hos = workorder.getHistoryOperations();
        WorkorderDto wd = new WorkorderDto();
        BeanUtils.copyProperties(workorder, wd);
        if (!CollectionUtils.isEmpty(workorder.getDeviceIds())) {
            List<WorkorderDevice> wds = workorder.getDeviceIds().stream().map(l -> {
                BusinessDeviceDto bdv = businessDeviceService.findById(l);
                if (bdv == null) {
                    return null;
                }
                return WorkorderDevice.toDto(bdv);
            }).collect(Collectors.toList());
            wd.setWorkorderDevice(wds);
        }
        if (!CollectionUtils.isEmpty(workorder.getLocationIds())) {
            List<Location> locations = locationService.findAll(workorder.getLocationIds());
            if (!CollectionUtils.isEmpty(locations)) {
                List<WorkorderLocation> wls = locations.stream().map(l
                        -> {
                    return WorkorderLocation.toDto(l);
                }).collect(Collectors.toList());
                wd.setWorkorderLocation(wls);
            }
        }
        wd.setSteps(workorder.getSteps());
        wd.setHistoryOperations(hos);
        return wd;
    }

    @Override
    public Page<Workorder> findAuditWorkorder(Specification<Workorder> specification) {
        Page<Workorder> all = workorderRepository.findAll(specification, JpaUtils.getPageRequest());
        return all;

    }

    @Override
    public Pagination<LocationWorkOrder> findWorkorderByTask(WorkorderCondition workoderCondiTion) {
        if (workoderCondiTion.getLocationId() != null) {
            List<Long> allChildren = locationService.getAllChildren(workoderCondiTion.getLocationId(), new ArrayList<Long>());
            List<String> collect = new ArrayList<>();
            collect.add(workoderCondiTion.getLocationId().toString());
            for (Long allChild : allChildren) {
                collect.add(allChild + "");
            }
            String str = collect.stream().collect(Collectors.joining(",", "(", ")"));
            workoderCondiTion.setLocationIds(str);
        }
        Pagination<LocationWorkOrder> page = workorderDao.findByCondition(workoderCondiTion);
        return page;
    }

    @Override
    public Page<Workorder> findAll(Specification<Workorder> specification) {
        Page<Workorder> all = workorderRepository.findAll(specification, JpaUtils.getPageRequest());
        return all;
    }

    @Override
    public void delete(Long id) {
        workorderRepository.deleteById(id);
    }

    @Override
    public DeviceWorkorderCount getCount(Long id) {
        Long arrange = locationWorkorderRepository.countFinished(StatusEnum.ARRANGE, id);
        Long accept = locationWorkorderRepository.countFinished(StatusEnum.ACCEPT, id);
        Long com = locationWorkorderRepository.countFinished(StatusEnum.COMPLETE, id);
        Long trace = locationWorkorderRepository.countFinished(StatusEnum.TRACE, id);
        Long end = locationWorkorderRepository.countFinished(StatusEnum.FINAL, id);
        Long terminate = locationWorkorderRepository.countFinished(StatusEnum.TERMINATE, id);
        Long all = locationWorkorderRepository.countAll(id);
        DeviceWorkorderCount dwc = new DeviceWorkorderCount(arrange, accept, com, trace, end, terminate, all);
        return dwc;
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean trace(Long workorderId, String advice, Long userId) {
        Workorder workorder = workorderRepository.findById(workorderId).orElse(null);
        if (workorder == null) {
            throw new BusinessException(ErrorEnum.NO_WORKORDER);
        }
        Task task =
                taskService.createTaskQuery().processInstanceId(workorder.getProcessId()).taskName(TaskNameEnum.TRACE_TASK).singleResult();
        if (task == null) {
            throw new BusinessException(ErrorEnum.NO_TASK);
        }
        EmployeeDto ed = employeeService.findByUserId(userId.toString());
        Map<String, Object> map = new HashMap<>(8);
        processService.completeTask(task.getId(), map);
        workorder.setStatus(StatusEnum.FINAL);
        workorderRepository.save(workorder);
        String info = "存档意见： " + advice;
        HistoryOperation ho = new HistoryOperation(StatusEnum.FINAL, ed.getName(),
                workorder, info);
        historyOperationRepository.save(ho);
        return true;
    }


    @Override
    public WorkorderReport getReport(String year) {
        String starMonth = year + "-01";
        String endMonth = year + "-12";
        Integer totalYear = workorderRepository.findYearWorkorder(year);
        Integer finaNormalYear = workorderRepository.findYearAndStatusWorkorder(year, StatusEnum.FINAL);
        Integer finalTerminalYear = workorderRepository.findYearAndStatusWorkorder(year, StatusEnum.TERMINATE);
        Integer unfish = totalYear - finalTerminalYear - finaNormalYear;
        List<Object[]> monthTotal = workorderRepository.findMonthWorkorder(starMonth, endMonth);
        List monthCounts = new ArrayList<MonthReport>();
        for (Object[] o : monthTotal) {
            MonthReport mc = new MonthReport();
            String s = String.valueOf(o[0]).split("-")[1];
            mc.setMonth(Integer.parseInt(s));
            int total = Integer.parseInt(String.valueOf(o[1]));
            mc.setTotal(total);
            Integer finalCount = workorderRepository.findMonthlyAndStatusWorkorder(String.valueOf(o[0]), StatusEnum.FINAL);
            Integer terminalCount = workorderRepository.findMonthlyAndStatusWorkorder(String.valueOf(o[0]), StatusEnum.TERMINATE);
            Integer unfishMonth = total - finalCount - terminalCount;
            mc.setNormalCount(finalCount);
            mc.setTerminalCount(terminalCount);
            mc.setUnfinishCount(unfishMonth);
            monthCounts.add(mc);
        }
        WorkorderReport wr = new WorkorderReport(totalYear, finaNormalYear, finalTerminalYear, unfish, monthCounts);
        return wr;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public Boolean arrangeMul(List<Long> ids, List<String> acceptors, Long userId, String workorderTeam) {
        ids.forEach(i -> {
            arrange(i, acceptors, userId);
        });
        workorderRepository.updateWorkorder(workorderTeam, ids);
        return true;
    }

    private void sendMsg(String id, Date time, String identity, String msg) {
        JobDetail startJob =
                JobBuilder.newJob(WorkorderMsgEndJob.class)
                        .withIdentity(identity, id)
                        .withDescription(msg)
                        .build();
        SimpleTrigger st = newTrigger()
                .withIdentity(identity, id)
                .startAt(time)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionNextWithRemainingCount())
                .build();
        try {
            scheduler.scheduleJob(startJob, st);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


}
