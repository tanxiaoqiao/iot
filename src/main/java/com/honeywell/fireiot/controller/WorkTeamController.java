package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.dto.TeamDto;
import com.honeywell.fireiot.dto.WorkTeamDto;
import com.honeywell.fireiot.entity.WorkTeam;
import com.honeywell.fireiot.service.WorkTeamService;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@RestController
@RequestMapping("/api/userTeam")
@Api(tags = "工作组操作")
public class WorkTeamController {

    @Autowired
    WorkTeamService workTeamService;


    @ApiOperation(value = "保存用户组", httpMethod = "POST")
    @PostMapping
    public ResponseObject save(@ApiParam @RequestBody WorkTeam workTeam) {
        workTeamService.addUserTeam(workTeam);
        return ResponseObject.success(null);
    }

    @ApiOperation(value = "删除用户组", httpMethod = "DELETE")
    @DeleteMapping
    @ApiImplicitParam(value = "id", name = "id")
    public ResponseObject delete(@RequestParam("id") String id) {
        workTeamService.deleteUserTeamById(id);
        return ResponseObject.success(null);
    }


    @ApiOperation(value = "获取所有用户组", httpMethod = "GET")
    @GetMapping
    public ResponseObject findAll() {
        List<WorkTeam> workTeam = workTeamService.findAllUserTeam();
        return ResponseObject.success(workTeam);
    }


    @ApiOperation(value = "获取所有用户组page", httpMethod = "GET")
    @GetMapping("/page")
    public ResponseObject findAllByCondition(@RequestParam(value = "pi", defaultValue = "0") Integer pi,
                                             @RequestParam(value = "ps", defaultValue = "5") Integer ps) {
        Pagination<WorkTeamDto> userPage = workTeamService.findUserPage(pi, ps);
        return ResponseObject.success(userPage);
    }

    @ApiOperation(value = "获取单个用户组", httpMethod = "GET")
    @GetMapping("/detail")
    @ApiImplicitParam(value = "id", name = "id")
    public ResponseObject<TeamDto> findTeamDtoOne(@RequestParam("id") String id) {
        TeamDto one = workTeamService.findTeamDtoOne(id);
        return ResponseObject.success(one);
    }


    @ApiOperation(value = "获取单个用户组对象类型", httpMethod = "GET")
    @GetMapping("/detailObject")
    @ApiImplicitParam(value = "id", name = "id")
    public ResponseObject<WorkTeamDto> findOne(@RequestParam("id") String id) {
        WorkTeamDto one = workTeamService.findOne(id);
        return ResponseObject.success(one);
    }

    @ApiOperation(value = "更新", httpMethod = "PATCH")
    @PatchMapping
    public ResponseObject update(@ApiParam @RequestBody WorkTeam workTeam) {
        workTeamService.updateUserTeam(workTeam);
        return ResponseObject.success(workTeam);
    }


}
