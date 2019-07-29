package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.dto.MaintenanceDto;
import com.honeywell.fireiot.entity.Maintenance;
import com.honeywell.fireiot.service.MaintenanceService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@RestController
@RequestMapping("/api/maintenance")
@Api(tags = "维保操作")
public class MaintenanceController {

    @Autowired
    MaintenanceService maintenanceService;

    @GetMapping
    @ApiOperation(value = "查询", httpMethod = "GET")
    @JpaPage
    public ResponseObject findAll() {
        Specification specification = JpaUtils.getSpecification();
        Page all = maintenanceService.findByCondition(specification);
        return ResponseObject.success(all);
    }

    @PostMapping
    @ApiOperation(value = "新增", httpMethod = "POST")
    public ResponseObject save(@RequestBody Maintenance maintenance) {
        maintenanceService.addMaintenance(maintenance);
        return ResponseObject.success(null);
    }

    @PatchMapping
    @ApiOperation(value = "修改", httpMethod = "PATCH")
    public ResponseObject update(@RequestBody Maintenance maintenance) {
        maintenanceService.updateMaintenance(maintenance);
        return ResponseObject.success(null);
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除", httpMethod = "DELETE")
    public ResponseObject delete(@PathVariable("id") Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseObject.success(null);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "详情", httpMethod = "GET")
    public ResponseObject getDaily(@PathVariable("id")Long id) {
        MaintenanceDto byid = maintenanceService.findByid(id);
        return ResponseObject.success(byid);
    }

    @GetMapping("/daily")
    @ApiOperation(value = "维保日历", httpMethod = "GET")
    @JpaPage
    public ResponseObject getDaily() {
        Page test = maintenanceService.getDailyByCondition(JpaUtils.getSpecification());
        return ResponseObject.success(test);
    }

}
