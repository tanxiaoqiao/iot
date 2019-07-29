package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.entity.AppPatrolSpot;
import com.honeywell.fireiot.service.AppInitInfoVersionService;
import com.honeywell.fireiot.service.AppPatrolSpotService;
import com.honeywell.fireiot.service.AppService;
import com.honeywell.fireiot.utils.ListPage;
import com.honeywell.fireiot.utils.ResponseObject;
import com.honeywell.fireiot.vo.AppInitInfoVersionVO;
import com.honeywell.fireiot.vo.AppPatrolVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppController
 * @author: dexter
 * @create: 2019-03-19 09:10
 * @description: 与手机端接口
 **/
@RestController
@RequestMapping("/api/app")
public class AppController {

    @Autowired
    private AppService appService;

    @Autowired
    private AppInitInfoVersionService appInitInfoVersionService;

    @Autowired
    private AppPatrolSpotService appPatrolSpotService;

    @GetMapping("/download/{employeeId}")
    public ResponseObject<ListPage<AppPatrolVO>> download(@PathVariable("employeeId") String id,
                                                          @RequestParam("pageSize") Integer pageSize,
                                                          @RequestParam("pageIndex") Integer pageIndex) {
        ListPage<AppPatrolVO> appPatrolVOListPage = appService.download(id, pageSize, pageIndex);

        return ResponseObject.success(appPatrolVOListPage);
    }

    @GetMapping("/version")
    public ResponseObject<AppInitInfoVersionVO> checkLatestVersion() {
        return ResponseObject.success(appInitInfoVersionService.check());
    }

    @PostMapping("/upload")
    public ResponseObject<String> upload(@RequestBody AppPatrolSpot appPatrolSpot){
        return ResponseObject.success(appPatrolSpotService.save(appPatrolSpot));
    }

}
