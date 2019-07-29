package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.entity.LocationWorkOrder;
import com.honeywell.fireiot.entity.Step;
import com.honeywell.fireiot.entity.Workorder;
import com.honeywell.fireiot.service.WorkorderService;
import com.honeywell.fireiot.utils.*;
import com.honeywell.fireiot.vo.SaveWorkorderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@RestController
@RequestMapping("/api/workorder")
@Api(tags = "工单操作")
public class WorkorderController {


    @Value("${workorder.path}")
    String path;
    @Autowired
    WorkorderService workorderService;


    @PatchMapping("/terminate")
    @ApiOperation(value = "终止", httpMethod = "PATCH")
    @JpaPage
    public ResponseObject terminate(@RequestBody Map<String, Object> data) {
        String workorderId = (String) data.get("workorderId");
        String reason = (String) data.get("reason");
        UserDto user = SessionUtils.getCurrentUser();
        Boolean terminate = workorderService.terminate(Long.valueOf(workorderId), reason, user.getId());
        return ResponseObject.success(terminate);
    }

    @GetMapping("/workorderId")
    @ApiOperation(value = "根据工单号查看详情", httpMethod = "GET")
    @ApiImplicitParam(value = "workorerId", name = "workorerId")
    public ResponseObject getDetailByWorkId(@RequestParam("workorerId") Long workorderId) {
        WorkorderDto one = workorderService.findWorkorderById(workorderId);
        return ResponseObject.success(one);
    }


    @PostMapping
    @ApiOperation(value = "新增工单", httpMethod = "POST")
    public ResponseObject add(@RequestBody @Validated @ApiParam SaveWorkorderVo saveWorkorderVo) throws IOException {
        UserDto user = SessionUtils.getCurrentUser();
        Long id = workorderService.addWorkorder(saveWorkorderVo, user.getId());
        if (id != null) {
            return ResponseObject.success(true);
        }
        return ResponseObject.success(false);
    }

    @PatchMapping("/audit")
    @ApiOperation(value = "审批工单", httpMethod = "PATCH")
    public ResponseObject pass(@ApiParam @RequestBody Map<String, Object> data) {
        String workorderId = (String) data.get("workorderId");
        String accept = (String) data.get("accept");
        String reason = (String) data.get("reason");
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.audit(Long.valueOf(workorderId), accept, user.getId(), reason);
        return ResponseObject.success(success);
    }


    @PatchMapping("/apply")
    @ApiOperation(value = "申请审批", httpMethod = "PATCH")
    public ResponseObject apply(@ApiParam @RequestBody Map<String, Object> data) {
        String workorderId = (String) data.get("workorderId");
        String reason = (String) data.get("reason");
        String auditor = (String) data.get("auditor");
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.applyAudit(Long.valueOf(workorderId), reason, user.getId(), auditor);
        return ResponseObject.success(success);
    }


    @PatchMapping("/trace")
    @ApiOperation(value = "存档", httpMethod = "PATCH")
    public ResponseObject trace(@RequestBody Map<String, Object> data) throws Exception {
        String workorderId = (String) data.get("workorderId");
        String reason = (String) data.get("reason");
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.trace(Long.valueOf(workorderId), reason, user.getId());
        return ResponseObject.success(success);
    }

    @PatchMapping("/complete")
    @ApiOperation(value = "完成", httpMethod = "PATCH")
    public ResponseObject complete(@RequestBody Map<String, Object> data) throws Exception {
        String workorderId = (String) data.get("workorderId");
        List<Step> steps = (List<Step>) data.get("steps");
        String attachUrl = (String) data.get("attachUrl");
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.complete(Long.valueOf(workorderId), user.getId(), steps, attachUrl);
        return ResponseObject.success(success);
    }


    @PatchMapping("/accept/{workorderId}")
    @ApiOperation(value = "接单", httpMethod = "PATCH")
    public ResponseObject accept(@PathVariable("workorderId") Long workorderId) {
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.accept(workorderId, user.getId());
        return ResponseObject.success(success);
    }

    @PatchMapping("/arrange")
    @ApiOperation(value = "派单", httpMethod = "PATCH")
    public ResponseObject arrange(@RequestBody Map<String, Object> data) {
        String workorderId = (String) data.get("workorderId");
        List<String> acceptors = (List<String>) data.get("acceptors");
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.arrange(Long.valueOf(workorderId), acceptors, user.getId());
        return ResponseObject.success(success);
    }


    @PatchMapping("/arrangeMult")
    @ApiOperation(value = "批量派发", httpMethod = "PATCH")
    public ResponseObject test(@RequestBody WorkorderInput data) {
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.arrangeMul(data.getIds(), data.getAcceptors(), user.getId(), data.getWorkorderTeam());
        return ResponseObject.success(success);
    }

    @PatchMapping("/refuse")
    @ApiOperation(value = "拒单", httpMethod = "PATCH")
    public ResponseObject refuse(@RequestBody Map<String, Object> data) {
        String workorderId = (String) data.get("workorderId");
        String reason = (String) data.get("reason");
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.refuse(Long.valueOf(workorderId), user.getId(), reason);
        return ResponseObject.success(success);
    }

    @PatchMapping("/update")
    @ApiOperation(value = "更新", httpMethod = "PATCH")
    public ResponseObject update(@RequestBody Workorder workorder) {
        UserDto user = SessionUtils.getCurrentUser();
        Boolean success = workorderService.updateWorkorder(workorder, user.getId());
        return ResponseObject.success(success);
    }


    @GetMapping("/deploy")
    @ApiOperation(value = "部署", httpMethod = "GET")
    void deploy() {
        workorderService.deploy();
    }


    @GetMapping("/audit")
    @ApiOperation(value = "查询待审核", httpMethod = "GET")
    @JpaPage
    public ResponseObject findAudit() {
        Page<Workorder> wo = workorderService.findAuditWorkorder(JpaUtils.getSpecification());
        return ResponseObject.success(wo);
    }

    @GetMapping
    @ApiOperation(value = "查询工单", httpMethod = "GET")
    public ResponseObject taskName(@RequestParam(value = "workorderId", required = false) Long workorderId,
                                   @RequestParam(value = "creator", required = false) String creator,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "status", required = false) List<Integer> status,
                                   @RequestParam(value = "type", required = false) Integer type,
                                   @RequestParam(value = "level", required = false) Integer level,
                                   @RequestParam(value = "pi", required = false) Integer pi,
                                   @RequestParam(value = "ps", required = false) Integer ps,
                                   @RequestParam(value = "createTimeStart", required = false) Timestamp createTimeStart,
                                   @RequestParam(value = "createTimeEnd", required = false) Timestamp createTimeEnd,
                                   @RequestParam(value = "employeeId", required = false) String employeeId,
                                   @RequestParam(value = "deviceId", required = false) Long deviceId,
                                   @RequestParam(value = "locationId", required = false) Long locationId) {
        WorkorderCondition wc = new WorkorderCondition();
        wc.setCreateTimeStart(createTimeStart);
        wc.setCreateTimeEnd(createTimeEnd);
        wc.setCreator(creator);
        wc.setId(workorderId);
        wc.setLevel(level);
        wc.setStatus(status);
        wc.setTitle(title);
        wc.setType(type);
        wc.setPi(pi);
        wc.setPs(ps);
        wc.setEmployeeId(employeeId);
        wc.setDeviceId(deviceId);
        wc.setLocationId(locationId);
        Pagination<LocationWorkOrder> list = workorderService.findWorkorderByTask(wc);
        return ResponseObject.success(list);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "上传图片", httpMethod = "POST")
    public ResponseObject upload(HttpServletRequest request) throws Exception {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        List<String> urls = new ArrayList<>(8);
        files.forEach(f -> {
            String name = f.getOriginalFilename();
            String fileName = UUID.randomUUID() + name.substring(name.lastIndexOf("."));
            String url = null;
            try {
                url = FileUtil.uploadFile(f.getBytes(), path, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            urls.add(url);
        });

        return ResponseObject.success(urls);
    }

    @GetMapping("/download")
    @ApiOperation(value = "下载图片", httpMethod = "GET")
    public void download(@RequestParam String url, HttpServletResponse response) throws Exception {
        FileUtil.downloadFile(url, response);
    }


    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除", httpMethod = "GET")
    public ResponseObject delete(@PathVariable("id") Long id) throws Exception {
        workorderService.delete(id);
        return ResponseObject.success(null);
    }

    @GetMapping("/count/{deviceId}")
    @ApiOperation(value = "根据deviceId查询工单数量", httpMethod = "GET")
    public ResponseObject getCount(@PathVariable("deviceId") Long id) throws Exception {
        DeviceWorkorderCount count = workorderService.getCount(id);
        return ResponseObject.success(count);
    }

    @GetMapping("/report/{year}")
    @ApiOperation(value = "报表", httpMethod = "GET")
    public ResponseObject report(@PathVariable("year") String year) throws Exception {
        WorkorderReport count = workorderService.getReport(year);
        return ResponseObject.success(count);
    }

    @GetMapping("/findAll")
    @ApiOperation(value = "查询", httpMethod = "GET")
    @JpaPage
    public ResponseObject report() throws Exception {
        Page all = workorderService.findAll(JpaUtils.getSpecification());
        return ResponseObject.success(all);
    }

}
