package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.RoleDto;
import com.honeywell.fireiot.entity.Resource;
import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.service.RoleService;
import com.honeywell.fireiot.utils.EnvHolder;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/14/2018 9:43 AM
 */
@RestController
@RequestMapping("/api/role")
@Api(tags = {"角色管理"})
public class RoleController {

    @Autowired
    RoleService roleService;


    @PostMapping
    @ApiOperation(value = "添加角色", httpMethod = "POST")
    public ResponseObject add(@RequestBody @ApiParam @Validated RoleDto roleDto) {
        // 判断角色是否已经存在
        if (!roleService.checkName(roleDto.getName(), roleDto.getSystemType())) {
            throw new BusinessException(ErrorEnum.ROLE_EXIST);
        }

        roleService.save(roleDto);
        return ResponseObject.success(null);
    }

    @DeleteMapping
    @ApiOperation(value = "删除角色", httpMethod = "DELETE")
    public ResponseObject delete(@RequestParam("id") Long id) {
        roleService.deleteById(id);
        return ResponseObject.success("OK");
    }

    @JpaPage
    @GetMapping
    @ApiOperation(value = "角色列表", httpMethod = "GET")
    public ResponseObject<Pagination<Role>> findPage() {
        Specification specification = JpaUtils.getSpecification().and((root, query, criteriaBuilder) -> {
            // 条件集合
            List<Predicate> predicates = new ArrayList<>();

            Predicate pre = criteriaBuilder.equal(root.get("systemType"), EnvHolder.getHolder().getSystemType());
            Predicate pre2 = criteriaBuilder.notEqual(root.get("name"), "ADMIN");

            predicates.add(pre);
            predicates.add(pre2);
            query.where(predicates.toArray(new Predicate[predicates.size()]));
            return query.getRestriction();
        });
        Page page = roleService.findPage(specification, JpaUtils.getPageRequest());
        Pagination<Role> pagination = new Pagination<Role>((int) page.getTotalElements(), page.getContent());
        return ResponseObject.success(pagination);
    }

    @GetMapping(value = "/all")
    public ResponseObject findAll() {
        List<Role> roleList = roleService.findAll(EnvHolder.getHolder().getSystemType());
        return ResponseObject.success(roleList);
    }

    @PutMapping
    @ApiOperation(value = "更新角色基本信息", httpMethod = "PUT")
    public ResponseObject update(@RequestBody @Validated @ApiParam RoleDto dto) {
        roleService.update(dto);
        return ResponseObject.success(null);
    }

    @GetMapping("/resources")
    @ApiOperation(value = "获取角色对应资源", httpMethod = "GET")
    @ApiImplicitParam(value = "id", name = "id")
    public ResponseObject<List<Resource>> getResource(@RequestParam("id") Long id) {
        List<Resource> list = roleService.getResources(id);
        return ResponseObject.success(list);
    }

    @PatchMapping("/resources")
    @ApiOperation(value = "更新角色权限", httpMethod = "PATCH")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "roleId", name = "roleId"),
            @ApiImplicitParam(value = "resourceIds", name = "resourceIds")
    })
    public ResponseObject updateResource(@RequestBody RoleDto dto) {
        Long[] resourceIds = dto.getResourceIds();
        roleService.updateRoleResourceRel(dto.getId(), resourceIds);
        return ResponseObject.success(null);
    }

    @PatchMapping("/menu")
    @ApiOperation(value = "更新角色的菜单", httpMethod = "PATCH")
    public ResponseObject updateMenu(@RequestBody RoleDto dto) {
        roleService.updateMenu(dto);
        return ResponseObject.success(null);
    }

    @GetMapping("/menu")
    @ApiOperation(value = "获取角色的菜单", httpMethod = "GET")
    public ResponseObject<String[]> getMenu(@RequestParam("id") Long id) {
        Role role = roleService.findById(id);
        if (role == null) {
            throw new BusinessException(ErrorEnum.ROLE_NOT_EXIST);
        }

        if (role.getMenuId() != null) {
            return ResponseObject.success(role.getMenuId().split(","));
        } else {
            return ResponseObject.success(null);
        }
    }
}
