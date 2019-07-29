package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.DepartmentDto;
import com.honeywell.fireiot.dto.TreeNodeDto;
import com.honeywell.fireiot.entity.Department;

import java.util.List;

/**
 * @project: fire-user
 * @name: DepartmentService
 * @author: dexter
 * @create: 2018-12-10 19:24
 * @description:
 **/
public interface DepartmentService {

    /**
     * save department
     * if id not null it will be updated
     *
     * @param department
     */
    void save(Department department);

    /**
     * soft delete department by id
     *
     * @param departmentId
     */
    void delete(String departmentId);

    /**
     * return department list
     *
     * @return
     */
    List<Department> findAll();

    /**
     * get department tree
     *
     * @return
     */
    TreeNodeDto<String> getTree();

    /**
     * recursively find child department
     *
     * @param list
     */
    void findChildren(List<TreeNodeDto<String>> list);

    /**
     * query department by id
     *
     * @param id
     * @return
     */
    DepartmentDto findById(String id);
}
