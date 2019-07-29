package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.dto.PollingDto;
import com.honeywell.fireiot.entity.Polling;
import com.honeywell.fireiot.service.PollingService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@RestController
@RequestMapping("/api/polling")
@Api(tags = "巡检操作")
public class PollingController {


    @Autowired
    PollingService pollingService;

    @PostMapping
    @ApiOperation(value = "添加巡检计划", httpMethod = "POST")
    public ResponseObject add(@ApiParam @RequestBody Polling polling) throws Exception {
     /*   UserDto user = SessionUtils.getCurrentUser();
        polling.setCreator(user.getId());*/
        Long id = pollingService.addPolling(polling);
        if (id > 0) {
            return ResponseObject.success(true);
        }
        return ResponseObject.fail(null);

    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除巡检计划", httpMethod = "DELETE")
    @ApiImplicitParam(name = "id", value = "id")
    public ResponseObject delete(@PathVariable("id") Long id) throws Exception {
        Boolean b = pollingService.deletePolling(id);
        return ResponseObject.success(b);

    }


    @GetMapping("/{id}")
    @ApiOperation(value = "巡检计划详情", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "id")
    public ResponseObject findOne(@PathVariable("id") Long id) throws Exception {
        PollingDto polling = pollingService.findone(id);
        return ResponseObject.success(polling);

    }

    @GetMapping
    @ApiOperation(value = "查询巡检计划", httpMethod = "GET")
    @JpaPage
    public ResponseObject findAll() {
        Pagination page = pollingService.findByCondition(JpaUtils.getSpecification());
        return ResponseObject.success(page);

    }


    @PatchMapping
    @ApiOperation(value = "修改巡检", httpMethod = "PATCH")
    public ResponseObject updateName(@ApiParam @RequestBody Polling polling) throws Exception {
        boolean b = pollingService.update(polling);
        return ResponseObject.success(b);
    }

    @PatchMapping("/activite")
    @ApiOperation(value = "激活/失效巡检", httpMethod = "PATCH")
    @ApiImplicitParams({@ApiImplicitParam(value = "id", name = "id"),
            @ApiImplicitParam(value = "activite", name = "activite")})
    public ResponseObject updateActivite(@RequestParam("id") Long id, @RequestParam("activite") Boolean activite) throws Exception {
        boolean b = pollingService.changeStatus(id, activite);
        return ResponseObject.success(b);

    }

    @GetMapping("/repair")
    public ResponseObject repair() throws Exception {
        pollingService.repairAllTrigger();
        return ResponseObject.success(null);

    }

}
