package com.honeywell.fireiot.service.impl;



import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dao.WorkTeamDao;
import com.honeywell.fireiot.dto.PositionDto;
import com.honeywell.fireiot.dto.TeamDto;
import com.honeywell.fireiot.dto.WorkTeamDto;
import com.honeywell.fireiot.entity.Employee;
import com.honeywell.fireiot.entity.WorkTeam;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.EmployeeRepository;
import com.honeywell.fireiot.repository.WorkTeamRepository;
import com.honeywell.fireiot.service.EmployeeService;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Service
public class WorkTeamServiceImpl implements WorkTeamService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    WorkTeamRepository workTeamRepository;

    @Autowired
    WorkTeamDao workTeamDao;


    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addUserTeam(WorkTeam workTeam) {
        String[] auditIds = workTeam.getAuditIds();
        String[] workerIds = workTeam.getWorkerIds();
        String[] verifyIds = workTeam.getVerifyIds();
        String[] tracerIds = workTeam.getTracerIds();
        String[] saveIds = workTeam.getSaveIds();
        List<String> audits = Arrays.asList(auditIds);
        Set<String> set = new HashSet<String>(8);
        set.addAll(Arrays.asList(workerIds));
        set.addAll(Arrays.asList(verifyIds));
        set.addAll(Arrays.asList(tracerIds));
        set.addAll(Arrays.asList(saveIds));
        set.addAll(audits);
        String[] all = set.toArray(new String[set.size()]);
        workTeam.setAllIds(all);
        WorkTeam save = workTeamRepository.save(workTeam);
        set.forEach(s -> {
            employeeService.saveWorkTeams(s, save.getId());
        });

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteUserTeamById(String id) {
        WorkTeam workTeam = workTeamRepository.findById(id).orElse(null);
        List<String> list = Arrays.asList(workTeam.getAllIds());
        list.forEach(s -> {
            employeeService.delete(s);
        });
        workTeamRepository.softDelete(id);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateUserTeam(WorkTeam workTeam) {
        WorkTeam update = workTeamDao.update(workTeam);
        List<String> list = Arrays.asList(update.getAllIds());
        list.forEach(s -> {
            employeeService.saveWorkTeams(s, update.getId());
        });

    }

    @Override
    public List<WorkTeam> findAllUserTeam() {
        List<WorkTeam> all = workTeamRepository.findAll();
        return all;
    }

    @Override
    public Pagination<WorkTeamDto> findUserPage(Integer pi, Integer ps) {
        Integer index = pi <= 0 ? 0 : pi - 1;
        Pageable page = PageRequest.of(index, ps, Sort.Direction.DESC, "createTime");
        Page<WorkTeam> all = workTeamRepository.findAll(page);
        List<WorkTeamDto> collect = all.stream().map(a -> {
            List<String> auditIds = Arrays.asList(a.getAuditIds());
            List<String> auditNames = new ArrayList<>(8);
            auditIds.forEach(w -> {
                Employee employee = employeeRepository.findById(w).orElse(null);
                if (employee != null) {
                    auditNames.add(employee.getName());
                }
            });
            List<String> workerIds = Arrays.asList(a.getWorkerIds());
            List<String> workerNames = new ArrayList<>(8);
            workerIds.forEach(w -> {
                Employee employee = employeeRepository.findById(w).orElse(null);
                if (employee != null) {
                    workerNames.add(employee.getName());
                }
            });
            List<String> tracerIds = Arrays.asList(a.getTracerIds());
            List<String> tracerNames = new ArrayList<>(8);
            tracerIds.forEach(w -> {
                Employee employee = employeeRepository.findById(w).orElse(null);
                if (employee != null) {
                    tracerNames.add(employee.getName());
                }
            });
            List<String> verifyIds = Arrays.asList(a.getVerifyIds());
            List<String> verifyNames = new ArrayList<>(8);
            verifyIds.forEach(w -> {
                Employee employee = employeeRepository.findById(w).orElse(null);
                if (employee != null) {
                    verifyNames.add(employee.getName());
                }
            });
            List<String> saveIds = Arrays.asList(a.getSaveIds());
            List<String> saveNames = new ArrayList<>(8);
            saveIds.forEach(w -> {
                Employee employee = employeeRepository.findById(w).orElse(null);
                if (employee != null) {
                    saveNames.add(employee.getName());
                }
            });
            List<String> allIds = Arrays.asList(a.getAllIds());
            List<String> allNames = new ArrayList<>(8);
            saveIds.forEach(w -> {
                Employee employee = employeeRepository.findById(w).orElse(null);
                if (employee != null) {
                    allNames.add(employee.getName());
                }
            });
            WorkTeamDto utd = new WorkTeamDto();
            utd.setId(a.getId());
            utd.setWorkerIds(workerIds);
            utd.setAuditIds(auditIds);
            utd.setTracerIds(tracerIds);
            utd.setVerifyIds(verifyIds);
            utd.setSaveIds(saveIds);
            utd.setAuditNames(auditNames);
            utd.setWorkerName(workerNames);
            utd.setTracerName(tracerNames);
            utd.setSaveNames(saveNames);
            utd.setVerifyNames(verifyNames);
            utd.setDescription(a.getDescription());
            utd.setTeamName(a.getTeamName());
            utd.setAllIds(allIds);
            utd.setAuditNames(allNames);
            return utd;
        }).collect(Collectors.toList());
        Pagination<WorkTeamDto> pa = new Pagination<WorkTeamDto>((int) all.getTotalElements(), collect);
        return pa;
    }

    @Override
    public WorkTeamDto findOne(String id) {
        WorkTeam workTeam = workTeamRepository.findById(id).orElse(null);
        if (workTeam == null) {
            throw new BusinessException(ErrorEnum.TEAM_NULL);
        }
        List<String> auditIds = Arrays.asList(workTeam.getAuditIds());
        List<String> auditNames = new ArrayList<>(8);
        auditIds.forEach(w -> {
            Employee employee = employeeRepository.findById(w).orElse(null);
            if (employee != null) {
                auditNames.add(employee.getName());
            }
        });
        List<String> workerIds = Arrays.asList(workTeam.getWorkerIds());
        List<String> workerNames = new ArrayList<>(8);
        workerIds.forEach(w -> {
            Employee employee = employeeRepository.findById(w).orElse(null);
            if (employee != null) {
                workerNames.add(employee.getName());
            }
        });
        List<String> tracerIds = Arrays.asList(workTeam.getTracerIds());
        List<String> tracerNames = new ArrayList<>(8);
        tracerIds.forEach(w -> {
            Employee employee = employeeRepository.findById(w).orElse(null);
            if (employee != null) {
                tracerNames.add(employee.getName());
            }
        });
        List<String> verifyIds = Arrays.asList(workTeam.getVerifyIds());
        List<String> verifyNames = new ArrayList<>(8);
        verifyIds.forEach(w -> {
            Employee employee = employeeRepository.findById(w).orElse(null);
            if (employee != null) {
                verifyNames.add(employee.getName());
            }
        });
        List<String> saveIds = Arrays.asList(workTeam.getSaveIds());
        List<String> saveNames = new ArrayList<>(8);
        saveIds.forEach(w -> {
            Employee employee = employeeRepository.findById(w).orElse(null);
            if (employee != null) {
                saveNames.add(employee.getName());
            }
        });

        List<String> allIds = Arrays.asList(workTeam.getAllIds());
        WorkTeamDto utd = new WorkTeamDto();
        utd.setId(id);
        utd.setWorkerIds(workerIds);
        utd.setAuditIds(auditIds);
        utd.setTracerIds(tracerIds);
        utd.setVerifyIds(verifyIds);
        utd.setSaveIds(saveIds);
        utd.setAuditNames(auditNames);
        utd.setWorkerName(workerNames);
        utd.setTracerName(tracerNames);
        utd.setSaveNames(saveNames);
        utd.setVerifyNames(verifyNames);
        utd.setAllIds(allIds);
        utd.setTeamName(workTeam.getTeamName());
        utd.setDescription(workTeam.getDescription());
        return utd;

    }

    @Override
    public TeamDto findTeamDtoOne(String id) {
        WorkTeam workTeam = workTeamRepository.findById(id).orElse(null);
        if (workTeam == null) {
            throw new BusinessException(ErrorEnum.TEAM_NULL);
        }
        List<String> auditIds = Arrays.asList(workTeam.getAuditIds());
        List<PositionDto> audits = auditIds.stream().map(w -> {
            PositionDto pd = new PositionDto();
            Employee employee = employeeRepository.findById(w).orElse(null);
            pd.setId(w);
            if (employee != null) {
                pd.setName(employee.getName());
            }
            return pd;
        }).collect(Collectors.toList());
        List<String> workerIds = Arrays.asList(workTeam.getWorkerIds());
        List<PositionDto> workers = workerIds.stream().map(w -> {
            PositionDto pd = new PositionDto();
            Employee employee = employeeRepository.findById(w).orElse(null);
            pd.setId(w);
            if (employee != null) {
                pd.setName(employee.getName());
            }
            return pd;
        }).collect(Collectors.toList());
        List<String> tracerIds = Arrays.asList(workTeam.getTracerIds());
        List<PositionDto> tracers = tracerIds.stream().map(w -> {
            PositionDto pd = new PositionDto();
            Employee employee = employeeRepository.findById(w).orElse(null);
            pd.setId(w);
            if (employee != null) {
                pd.setName(employee.getName());
            }
            return pd;
        }).collect(Collectors.toList());
        List<String> verifyIds = Arrays.asList(workTeam.getVerifyIds());
        List<PositionDto> verifys = verifyIds.stream().map(w -> {
            PositionDto pd = new PositionDto();
            Employee employee = employeeRepository.findById(w).orElse(null);
            pd.setId(w);
            if (employee != null) {
                pd.setName(employee.getName());
            }
            return pd;
        }).collect(Collectors.toList());
        List<String> saveIds = Arrays.asList(workTeam.getSaveIds());
        List<PositionDto> saves = saveIds.stream().map(w -> {
            PositionDto pd = new PositionDto();
            Employee employee = employeeRepository.findById(w).orElse(null);
            pd.setId(w);
            if (employee != null) {
                pd.setName(employee.getName());
            }
            return pd;
        }).collect(Collectors.toList());

        List<String> allIds = Arrays.asList(workTeam.getAllIds());
        List<PositionDto> alls = allIds.stream().map(w -> {
            PositionDto pd = new PositionDto();
            Employee employee = employeeRepository.findById(w).orElse(null);
            pd.setId(w);
            if (employee != null) {
                pd.setName(employee.getName());
            }
            return pd;
        }).collect(Collectors.toList());
        TeamDto utd = new TeamDto();
        utd.setId(id);
        utd.setWorkers(workers);
        utd.setAudits(audits);
        utd.setTracers(tracers);
        utd.setVerifys(verifys);
        utd.setSaves(saves);
        utd.setAlls(alls);
        utd.setTeamName(workTeam.getTeamName());
        utd.setDescription(workTeam.getDescription());
        return utd;

    }



    @Override
    public void updateWorkTeamByEmp(String workteamId, String empId, Integer type) {
        WorkTeam workTeam = workTeamRepository.findById(workteamId).orElse(null);
        if (workTeam == null) {
            throw new BusinessException(ErrorEnum.NO_WORKTEAM);
        }

        List<String> au = Arrays.asList(workTeam.getAuditIds());
        ArrayList<String> audit = new ArrayList<>(au);
        List<String> alls = Arrays.asList(workTeam.getAllIds());
        ArrayList<String> allIds = new ArrayList<>(alls);
        //普通员工
        if (type == 0) {
            //之前是否主管
            if (audit.contains(empId)) {
                audit.remove(empId);
                String[] audits = audit.toArray(new String[audit.size()]);
                workTeam.setAuditIds(audits);
            }
            if (!allIds.contains(empId)) {
                allIds.add(empId);
                String[] all = allIds.toArray(new String[allIds.size()]);
                workTeam.setAllIds(all);
            }

            //主管
        } else {
            if (!audit.contains(empId)) {
                audit.add(empId);
                String[] audits = audit.toArray(new String[audit.size()]);
                workTeam.setAuditIds(audits);
            }
            if (!allIds.contains(empId)) {
                allIds.add(empId);
                String[] all = allIds.toArray(new String[allIds.size()]);
                workTeam.setAllIds(all);
            }

        }
        workTeamRepository.save(workTeam);


    }

}
