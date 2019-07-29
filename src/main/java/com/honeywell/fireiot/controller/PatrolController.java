package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.PatrolTitle;
import com.honeywell.fireiot.dto.MobPatrolVo;
import com.honeywell.fireiot.dto.PatrolCondition;
import com.honeywell.fireiot.dto.PatrolReport;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.InspectionResult;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.entity.Spot;
import com.honeywell.fireiot.service.EmpPatrolService;
import com.honeywell.fireiot.service.EmployeeService;
import com.honeywell.fireiot.service.PatrolService;
import com.honeywell.fireiot.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@RestController
@RequestMapping("/api/patrol")
@Api(tags = "巡检历史操作")
public class PatrolController {


    @Autowired
    PatrolService patrolService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmpPatrolService empPatrolService;


    @GetMapping
    @ApiOperation(value = "查询巡检历史", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "actEndTime", value = "actEndTime"),
            @ApiImplicitParam(name = "actStartTime", value = "actStartTime"),
            @ApiImplicitParam(name = "preEndTime", value = "preEndTime"),
            @ApiImplicitParam(name = "preStartTime", value = "preStartTime"),
            @ApiImplicitParam(name = "status", value = "status"),
            @ApiImplicitParam(name = "spotStatus", value = "spotStatus"),
            @ApiImplicitParam(name = "ps", value = "ps"),
            @ApiImplicitParam(name = "pi", value = "pi"),
    })
    public ResponseObject findAll(@RequestParam(value = "actEndTime", required = false) Timestamp actEndtime,
                                  @RequestParam(value = "actStartTime", required = false) Timestamp actStarttime,
                                  @RequestParam(value = "preEndTime", required = false) Timestamp preEndtime,
                                  @RequestParam(value = "preStartTime", required = false) Timestamp preStarttime,
                                  @RequestParam(value = "status", required = false) Integer status,
                                  @RequestParam(value = "spotStatus", required = false) Integer spotStatus,
                                  @RequestParam(value = "ps", required = false, defaultValue = "10") Integer ps,
                                  @RequestParam(value = "pi", required = false, defaultValue = "1") Integer pi) {
        PatrolCondition condition = new PatrolCondition();
        condition.setActEndTime(actEndtime);
        condition.setActStartTime(actStarttime);
        condition.setPi(pi);
        condition.setPs(ps);
        condition.setStatus(status);
        condition.setPreEndTime(preEndtime);
        condition.setPreStartTime(preStarttime);
        condition.setSpotStatus(spotStatus);
        Pagination<Patrol> page = patrolService.findByCondition(condition);
        return ResponseObject.success(page);

    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据patrolId获取spot信息", httpMethod = "GET")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        List<Spot> list = patrolService.findone(id);
        return ResponseObject.success(list);

    }

    @GetMapping("/{patrolId}/{spotId}")
    @ApiOperation(value = "根据patrol和spotId获取综合巡检信息", httpMethod = "GET")
    public ResponseObject findSpotOne(@PathVariable("patrolId") Long patrolId,
                                      @PathVariable("spotId") Long spotId) {
        List<InspectionResult> list = patrolService.findSpotOne(patrolId, spotId);
        return ResponseObject.success(list);

    }

    @GetMapping("/device/{patrolId}/{spotId}")
    @ApiOperation(value = "根据patrol和spotId获取设备信息", httpMethod = "GET")
    public ResponseObject findSpotDevice(@PathVariable("patrolId") Long patrolId,
                                         @PathVariable("spotId") Long spotId) {
        List<BusinessDevice> list = patrolService.findSpotDevice(patrolId, spotId);
        return ResponseObject.success(list);

    }

    @GetMapping("/{patrolId}/{spotId}/{deviceId}")
    @ApiOperation(value = "巡检device巡检信息", httpMethod = "GET")
    public ResponseObject findPatrolSpotDevice(@PathVariable("patrolId") Long patrolId,
                                               @PathVariable("spotId") Long spotId, @PathVariable("patrolId") Long deviceId) {
        List<InspectionResult> list = patrolService.findDeviceOne(patrolId, spotId, deviceId);
        return ResponseObject.success(list);

    }


    @GetMapping("/undo/{empId}")
    @ApiOperation(value = "手机端获取未完成巡检", httpMethod = "GET")
    public ResponseObject undo(@PathVariable("empId") String empId) {
        List<MobPatrolVo> patrol = empPatrolService.findByEmpId(empId);
        return ResponseObject.success(patrol);

    }

    @GetMapping("/excel")
    @ApiOperation(value = "导出数据", httpMethod = "GET")
    public ResponseObject download(@RequestParam(value = "actEndTime", required = false) Timestamp actEndtime,
                                   @RequestParam(value = "actStartTime", required = false) Timestamp actStarttime,
                                   @RequestParam(value = "preEndTime", required = false) Timestamp preEndtime,
                                   @RequestParam(value = "preStartTime", required = false) Timestamp preStarttime,
                                   @RequestParam(value = "status", required = false) Integer status,
                                   HttpServletResponse response) throws Exception {
        String fileName = UUID.randomUUID() + ".xlsx";
        //构造excelData
        ExcelData data = new ExcelData();
        data.setFileName(fileName);
        data.setTitle(PatrolTitle.PATROL_LIST);
        PatrolCondition condition = new PatrolCondition();
        condition.setActEndTime(actEndtime);
        condition.setActStartTime(actStarttime);
        condition.setStatus(status);
        condition.setPreEndTime(preEndtime);
        condition.setPreStartTime(preStarttime);
        Pagination<Patrol> patrols = patrolService.findByCondition(condition);
        if (null == patrols) {
            return ResponseObject.fail(ErrorEnum.NO_POTRAL);
        }

        //构造表格数据
        List<List<Object>> rowData = new ArrayList<>();
        for (Patrol pa : patrols.getDataList()) {
            List<Object> row = new ArrayList<>();
            row.add(pa.getName());
            row.add(pa.getPreStartTime());
            row.add(pa.getPreEndTime());
            row.add(pa.getActStartTime());
            row.add(pa.getActEndTime());
            row.add(pa.getStatus());
            rowData.add(row);
        }
        data.setRows(rowData);

        ExcelUtil.exportExcel(response, data);
        return ResponseObject.success(null);

    }


    @GetMapping("/report/{year}")
    ResponseObject report(@PathVariable("year") String year) {
        PatrolReport report = patrolService.report(year);
        return ResponseObject.success(report);
    }

    @PostMapping
    ResponseObject add(@RequestBody Patrol patrol) {
        Boolean b = patrolService.add(patrol);
        return ResponseObject.success(b);
    }
}
