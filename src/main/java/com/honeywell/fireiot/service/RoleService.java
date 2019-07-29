package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.RoleDto;
import com.honeywell.fireiot.entity.Resource;
import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/14/2018 9:48 AM
 */
public interface RoleService {

    void save(RoleDto dto);

    void update(RoleDto dto);

    void deleteById(Long roleId);

    Role findById(Long roleId);

    Pagination<Role> findAll(Integer pi, Integer ps);

    List<Role> findAdmin();

    boolean checkName(String name, String systemType);

    List<Resource> getResources(Long roleId);

    void updateRoleResourceRel(Long roleId, Long[] resourceIds);

    void updateMenu(RoleDto dto);

    Page findPage(Specification specification, Pageable pageable);

    List<Role> findAll();

    List<Role> findAll(String systemType);

    Role toEntity(RoleDto dto);

}
