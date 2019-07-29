package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.dto.EmployeeDto;
import com.honeywell.fireiot.dto.EmployeeRelationsDto;
import com.honeywell.fireiot.entity.Employee;
import com.honeywell.fireiot.entity.EmployeeRelations;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.repository.EmployeeRelationsRepository;
import com.honeywell.fireiot.repository.EmployeeRepository;
import com.honeywell.fireiot.service.EmployeeService;
import com.honeywell.fireiot.service.FileUploadService;
import com.honeywell.fireiot.service.UserService;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.utils.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * @project: fire-user
 * @name: EmployeeServiceImpl
 * @author: dexter
 * @create: 2018-12-19 14:42
 * @description:
 **/
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    public static final String SPLIT = "\\|";

    public static final String FUZZY_SIGN = "%";

    public static final String IMAGE_PATH = "fire/employee/";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeRelationsRepository employeeRelationsRepository;

    @Autowired
    private WorkTeamService workTeamService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void save(EmployeeDto employeeDto) {
        Employee employee = new Employee();

        BeanUtils.copyProperties(employeeDto, employee);
        employee = employeeRepository.save(employee);

        if (employeeDto.getEmployeeRelationsDto() != null) {
            EmployeeRelations employeeRelations = new EmployeeRelations();
            BeanUtils.copyProperties(employeeDto.getEmployeeRelationsDto(), employeeRelations);
            employeeRelations.setEmployeeId(employee.getId());
            Optional<EmployeeRelations> employeeRelationsOptional = employeeRelationsRepository.findByEmployeeId(employee.getId());
            if (employeeRelationsOptional.isPresent()) {
                employeeRelations.setId(employeeRelationsOptional.get().getId());
            }
            employeeRelationsRepository.save(employeeRelations);

            // 若工作组不为空，则调用工作组服务
            if (!StringUtils.isEmpty(employeeRelations.getWorkTeamIds())) {
                if (employeeRelations.getWorkTeamIds().replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length()!=0) {

                    String[] workTeamArray = employeeRelations.getWorkTeamIds().split(SPLIT);
                    for (int i = 0; i < workTeamArray.length; i++) {
                        workTeamService.updateWorkTeamByEmp(workTeamArray[i], employee.getId(), employee.getType());
                    }
                } else {
                    workTeamService.updateWorkTeamByEmp(employeeRelations.getWorkTeamIds(), employee.getId(), employee.getType());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void delete(String employeeId) {
        employeeRelationsRepository.softDelete(employeeId);
        employeeRepository.softDelete(employeeId);
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public EmployeeDto findById(String id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            EmployeeDto employeeDto = new EmployeeDto(employee);

            Optional<EmployeeRelations> employeeRelationsOptional = employeeRelationsRepository.findByEmployeeId(id);
            if (employeeRelationsOptional.isPresent()) {

                User user = null;
                if (!StringUtils.isEmpty(employeeRelationsOptional.get().getUserId())) {
                    user = userService.findUserById(Long.parseLong(employeeRelationsOptional.get().getUserId()));
                }

                EmployeeRelationsDto relationsDto = new EmployeeRelationsDto(employeeRelationsOptional.get());

                if (user != null) {
                    relationsDto.setUserName(user.getName());
                }
                employeeDto.setEmployeeRelationsDto(relationsDto);
            }

            return employeeDto;
        }

        return null;
    }

    @Override
    public EmployeeDto findByUserId(String userId) {
        Optional<EmployeeRelations> employeeRelationsOptional = employeeRelationsRepository.findByUserId(userId);

        if (employeeRelationsOptional.isPresent()) {

            User user = null;
            if (!StringUtils.isEmpty(employeeRelationsOptional.get().getUserId())) {
                user = userService.findUserById(Long.parseLong(employeeRelationsOptional.get().getUserId()));
            }

            EmployeeRelationsDto employeeRelationsDto = new EmployeeRelationsDto(employeeRelationsOptional.get());

            if (user != null) {
                employeeRelationsDto.setUserName(user.getName());
            }

            Optional<Employee> optionalEmployee = employeeRepository.findById(employeeRelationsDto.getEmployeeId());

            EmployeeDto employeeDto = null;

            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();
                employeeDto = new EmployeeDto(employee);

                employeeDto.setEmployeeRelationsDto(employeeRelationsDto);
            }

            return employeeDto;
        }

        return null;
    }

    @Override
    public void saveWorkTeams(String employeeId, String workTeamId) {
        Optional<EmployeeRelations> employeeRelations = employeeRelationsRepository.findByEmployeeId(employeeId);

        if (employeeRelations.isPresent() && !StringUtils.isEmpty(workTeamId)) {
            if (!StringUtils.isEmpty(employeeRelations.get().getWorkTeamIds())
                    && !employeeRelations.get().getWorkTeamIds().contains(workTeamId)) {
                employeeRelations.get().setWorkTeamIds(employeeRelations.get().getWorkTeamIds() + SPLIT + workTeamId);
            }
            if (StringUtils.isEmpty(employeeRelations.get().getWorkTeamIds())) {
                employeeRelations.get().setWorkTeamIds(workTeamId);
            }

            employeeRelationsRepository.save(employeeRelations.get());
        }
    }

    @Override
    public Pagination<Employee> findAllPage(Integer pageIndex, Integer pageSize) {
        int size = pageIndex <= 0 ? 0 : (pageIndex - 1);
        PageRequest pageRequest = PageRequest.of(size, pageSize, Sort.Direction.DESC, "updateTime");
        Page<Employee> all = employeeRepository.findAll(pageRequest);
        return new Pagination<>((int) all.getTotalElements(), all.getContent());
    }

    @Override
    public List<Employee> fuzzyQuery(String name) {
        return employeeRepository.findByNameLike(FUZZY_SIGN + name + FUZZY_SIGN);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null) {
            return "";
        }
        try {
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());

            Optional<String> url = fileUploadService.uploadFile(inputStream, IMAGE_PATH + file.getName());

            log.info(url.get());

            if (StringUtils.isEmpty(url.get())) {
                return "";
            } else {
                return url.get();
            }

        } catch (IOException e) {
            return "";
        }
    }
}
