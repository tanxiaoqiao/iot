package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.EmployeeDto;
import com.honeywell.fireiot.entity.Employee;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @project: fire-user
 * @name: EmployeeService
 * @author: dexter
 * @create: 2018-12-19 14:23
 * @description:
 **/
public interface EmployeeService {

    /**
     * save employeeDto
     *
     * @param employeeDto
     */
    void save(EmployeeDto employeeDto);

    /**
     * soft delete employee by id
     *
     * @param employeeId
     */
    void delete(String employeeId);

    /**
     * return employee list
     *
     * @return
     */
    List<Employee> findAll();

    /**
     * query employee by id
     *
     * @param id
     * @return
     */
    EmployeeDto findById(String id);

    /**
     * query employee by user id
     *
     * @param userId
     * @return
     */
    EmployeeDto findByUserId(String userId);

    /**
     * for workTeam to save relations between employee and workTeams
     *
     * @param employeeId
     * @param workTeamId
     */
    void saveWorkTeams(String employeeId, String workTeamId);

    /**
     * pagination
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Pagination<Employee> findAllPage(Integer pageIndex, Integer pageSize);

    /**
     * 根据员工名称模糊查询
     *
     * @param name
     * @return
     */
    List<Employee> fuzzyQuery(String name);

    /**
     * 上传图片至文件服务器，返回路径
     *
     * @param file
     * @return
     */
    String uploadImage(MultipartFile file);
}
