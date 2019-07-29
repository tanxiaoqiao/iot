package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.dto.DepartmentDto;
import com.honeywell.fireiot.dto.TreeNodeDto;
import com.honeywell.fireiot.entity.Department;
import com.honeywell.fireiot.repository.DepartmentRepository;
import com.honeywell.fireiot.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @project: fire-user
 * @name: DepartmentServiceImpl
 * @author: dexter
 * @create: 2018-12-11 10:14
 * @description:
 **/
@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    public static final String TOP_PARENT_ID = "-1";

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void save(Department department) {
        departmentRepository.save(department);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void delete(String departmentId) {
        departmentRepository.softDelete(departmentId);
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public TreeNodeDto<String> getTree() {
        List<TreeNodeDto<String>> list = this.findByParent(TOP_PARENT_ID);
        this.findChildren(list);
        // query root node
        TreeNodeDto<String> root = new TreeNodeDto<>();
        root.setLeaf(false);
        root.setId(TOP_PARENT_ID);
        root.setChildren(list);
        root.setName("Root");
        root.setLevel(-1);
        return root;
    }

    /**
     * recursively find child department
     *
     * @param list
     */
    @Override
    public void findChildren(List<TreeNodeDto<String>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.stream()
                .forEach(node -> {
                    List<TreeNodeDto<String>> children = this.findByParent(node.getId());
                    if (CollectionUtils.isEmpty(children)) {
                        node.setLeaf(true);
                        node.setChildren(Collections.emptyList());
                        return;
                    }
                    this.findChildren(children);
                    node.setChildren(children);
                    node.setLeaf(false);
                });
    }

    @Override
    public DepartmentDto findById(String id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        if (optionalDepartment.isPresent()) {
            DepartmentDto departmentDto = new DepartmentDto(optionalDepartment.get());
            departmentDto.setFullName(
                    this.getFullName(optionalDepartment.get().getParentId(), optionalDepartment.get().getName()));
            return departmentDto;
        } else {
            return null;
        }
    }

    /**
     * convert department object to TreeNodeDto
     *
     * @param department
     * @return
     */
    private TreeNodeDto<String> convert(Department department) {
        TreeNodeDto<String> treeNode = new TreeNodeDto();
        treeNode.setId(department.getId());
        treeNode.setParentId(department.getParentId());
        treeNode.setLevel(department.getLevel());
        treeNode.setName(department.getName());
        return treeNode;
    }

    /**
     * find parent node
     *
     * @param id
     * @return
     */
    private List<TreeNodeDto<String>> findByParent(String id) {
        return departmentRepository.findByParentId(id)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    /**
     * get full department name
     *
     * @param parentId
     * @return
     */
    private String getFullName(String parentId, String name) {
        if (parentId.equals(TOP_PARENT_ID)) {
            return name;
        } else {
            Optional<Department> parentNode = departmentRepository.findById(parentId);
            if (parentNode.isPresent()) {
                name = parentNode.get().getName() + " / " + name;
                return this.getFullName(parentNode.get().getParentId(), name);
            } else {
                return "";
            }
        }
    }
}
