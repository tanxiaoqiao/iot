package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.dto.DepartmentDto;
import com.honeywell.fireiot.dto.TreeNodeDto;
import com.honeywell.fireiot.entity.Department;
import com.honeywell.fireiot.service.DepartmentService;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @project: fire-user
 * @name: DepartmentController
 * @author: dexter
 * @create: 2018-12-10 19:22
 * @description:
 **/
@RestController
@RequestMapping("/api/department")
@Api(tags = {"部门管理"})
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/tree")
    @ApiOperation(value = "获得部门树", httpMethod = "GET")
    public ResponseObject<TreeNodeDto<String>> getTree() {
        return ResponseObject.success(departmentService.getTree());
    }

    @GetMapping("/{id}")
    @ApiImplicitParam(name = "id", value = "id")
    @ApiOperation(value = "获取部门详情", httpMethod = "GET")
    public ResponseObject<DepartmentDto> info(@PathVariable("id") String id) {
        return ResponseObject.success(departmentService.findById(id));
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存部门", httpMethod = "POST")
    public ResponseObject save(@RequestBody @ApiParam Department department) {
        departmentService.save(department);
        return ResponseObject.success(null);
    }

    @DeleteMapping("/{id}")
    @ApiImplicitParam(name = "id", value = "id")
    @ApiOperation(value = "删除部门", httpMethod = "DELETE")
    public ResponseObject delete(@PathVariable("id") String id) {
        departmentService.delete(id);
        return ResponseObject.success(null);
    }

}
