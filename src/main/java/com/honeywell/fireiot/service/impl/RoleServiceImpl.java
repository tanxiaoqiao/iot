package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.RoleDto;
import com.honeywell.fireiot.entity.Resource;
import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.entity.RoleResourceRel;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.RoleRepository;
import com.honeywell.fireiot.service.ResourceService;
import com.honeywell.fireiot.service.RoleResourceRelService;
import com.honeywell.fireiot.service.RoleService;
import com.honeywell.fireiot.service.config.RedisResourceInitService;
import com.honeywell.fireiot.utils.Pagination;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @Author: Kris
 * @Author: zhenzhong.wang
 * @Description:
 * @Date: 8/14/2018 9:52 AM
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ResourceService resourceService;
    @Autowired
    RoleResourceRelService roleResourceRelService;

    @Autowired
    RedisResourceInitService systemConfigService;

    @Override
    public void save(RoleDto dto) {
        Role entity = toEntity(dto);
        Role role = roleRepository.save(entity);

        if (dto.getResourceIds() != null) {
            updateRoleResourceRel(role.getId(), dto.getResourceIds());
        }
    }

    @Override
    public void update(RoleDto dto) {
        Role oldRole = roleRepository
                .findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorEnum.ROLE_NOT_EXIST));

        Role entity = toEntity(dto);

        BeanUtils.copyProperties(entity, oldRole, "users", "menuId");

        roleRepository.save(oldRole);
    }

    @Override
    public void deleteById(Long roleId) {
        roleRepository.deleteById(roleId);

        systemConfigService.refreshResource();
    }

    @Override
    public Role findById(Long roleId) {
        return roleRepository.findById(roleId).get();
    }

    @Override
    public Pagination<Role> findAll(Integer pi, Integer ps) {
        Integer index = pi <= 0 ? 0 : pi - 1;
        Pageable page = PageRequest.of(index, ps, Sort.Direction.DESC, "id");
        Page<Role> all = roleRepository.findAll(page);
        Pagination<Role> pa = new Pagination<Role>((int) all.getTotalElements(), all.getContent());
        return pa;
    }

    @Override
    public List<Role> findAdmin() {
        return roleRepository.findAdmin();
    }

    @Override
    public boolean checkName(String name, String systemType) {
        if (roleRepository.findByNameAndSystemType(name, systemType) == null) {
            return true;
        }
        return false;
    }

    @Override
    public List<Resource> getResources(Long roleId) {
        List<Resource> resources = roleResourceRelService.getResourcesByRole(roleId);
        return resources;
    }

    @Override
    public void updateRoleResourceRel(Long roleId, Long[] resourceIds) {
        // 先删除之前的角色资源关系
        roleResourceRelService.deleteByRoleId(roleId);

        // 构建新的roleResource关系并存储
        Role role = roleRepository.getOne(roleId);
        List<RoleResourceRel> newResources = new ArrayList<>();
        for (Long r : resourceIds) {
            Optional.of(resourceService.findOne(r))
                    .ifPresent((resource) -> {
                        RoleResourceRel rel = new RoleResourceRel(role, resource);
                        newResources.add(rel);
                    });
        }
        roleResourceRelService.save(newResources);

        systemConfigService.refreshResource();
    }

    @Override
    public void updateMenu(RoleDto dto) {
        Role role = roleRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorEnum.ROLE_NOT_EXIST));
        role.setMenuId(ArrayUtils.toString(dto.getMenuIds())
                .replaceAll("\\{|\\}", ""));
        roleRepository.save(role);
    }

    @Override
    public Page findPage(Specification specification, Pageable pageable) {
        return roleRepository.findAll(specification, pageable);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> findAll(String systemType) {

        Specification specification = (root, query, criteriaBuilder) -> {
            // 条件集合
            List<Predicate> predicates = new ArrayList<>();

            Predicate pre = criteriaBuilder.equal(root.get("systemType"), systemType);
            Predicate pre2 = criteriaBuilder.notEqual(root.get("name"), "ADMIN");

            predicates.add(pre);
            predicates.add(pre2);
            query.where(predicates.toArray(new Predicate[predicates.size()]));
            return query.getRestriction();
        };
        return roleRepository.findAll(specification);
    }

    @Override
    public Role toEntity(RoleDto dto) {
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);

        // 转换菜单ID
        if (dto.getMenuIds() != null) {
            String menuId = ArrayUtils.toString(dto.getMenuIds())
                    .replaceAll("\\{|\\}", "");
            role.setMenuId(menuId);
        }
        return role;
    }

}
