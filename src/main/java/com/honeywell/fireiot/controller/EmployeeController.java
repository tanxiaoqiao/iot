package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.dto.EmployeeDto;
import com.honeywell.fireiot.entity.Employee;
import com.honeywell.fireiot.service.EmployeeService;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @project: fire-user
 * @name: EmployeeController
 * @author: dexter
 * @create: 2018-12-20 15:44
 * @description:
 **/
@RestController
@RequestMapping("/api/employee")
@Api(tags = {"员工管理"})
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/save")
    @ApiOperation(value = "保存员工", httpMethod = "POST")
    public ResponseObject save(@RequestBody @ApiParam EmployeeDto employeeDto) {
        employeeService.save(employeeDto);
        return ResponseObject.success(null);
    }

    @GetMapping("/{id}")
    @ApiImplicitParam(name = "id", value = "id")
    @ApiOperation(value = "获取员工详情", httpMethod = "GET")
    public ResponseObject<EmployeeDto> info(@PathVariable("id") String id) {
        return ResponseObject.success(employeeService.findById(id));
    }

    @GetMapping("/query/{userId}")
    @ApiImplicitParam(name = "userId", value = "userId")
    @ApiOperation(value = "根据用户id获取员工详情", httpMethod = "GET")
    public ResponseObject<EmployeeDto> findByUserId(@PathVariable("userId") String userId) {
        return ResponseObject.success(employeeService.findByUserId(userId));
    }

    @DeleteMapping("/{id}")
    @ApiImplicitParam(name = "id", value = "id")
    @ApiOperation(value = "删除员工", httpMethod = "DELETE")
    public ResponseObject delete(@PathVariable("id") String id) {
        employeeService.delete(id);
        return ResponseObject.success(null);
    }

    @GetMapping("/list")
    @ApiOperation(value = "返回所有员工", httpMethod = "GET")
    public ResponseObject<List<Employee>> list() {
        return ResponseObject.success(employeeService.findAll());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页所有员工", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex"),
            @ApiImplicitParam(name = "pageSize")
    })
    public ResponseObject<Pagination<Employee>> findAllPage(@RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
                                                            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        return ResponseObject.success(employeeService.findAllPage(pageIndex, pageSize));
    }

    @GetMapping("/fuzzy")
    public ResponseObject<List<Employee>> fuzzyQuery(@RequestParam("name") String name) {
        return ResponseObject.success(employeeService.fuzzyQuery(name));
    }

    @PostMapping("/upload")
    @ApiOperation("上传员工头像")
    public ResponseObject<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseObject.success(employeeService.uploadImage(file));
    }
}
