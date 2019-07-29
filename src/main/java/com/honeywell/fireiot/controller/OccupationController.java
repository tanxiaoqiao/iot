package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.entity.Occupation;
import com.honeywell.fireiot.service.OccupationService;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @project: fire-user
 * @name: OccupationController
 * @author: dexter
 * @create: 2018-12-18 17:05
 * @description:
 **/
@RestController
@RequestMapping("/api/occupation")
@Api(tags = {"岗位管理"})
public class OccupationController {

    @Autowired
    private OccupationService occupationService;

    @GetMapping("/{id}")
    @ApiImplicitParam(name = "id", value = "id")
    @ApiOperation(value = "获取岗位详情", httpMethod = "GET")
    public ResponseObject<Occupation> info(@PathVariable("id") String id) {
        return ResponseObject.success(occupationService.findById(id));
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存岗位", httpMethod = "POST")
    public ResponseObject save(@RequestBody @ApiParam Occupation occupation) {
        occupationService.save(occupation);
        return ResponseObject.success(null);
    }

    @DeleteMapping("/{id}")
    @ApiImplicitParam(name = "id", value = "id")
    @ApiOperation(value = "删除岗位", httpMethod = "DELETE")
    public ResponseObject delete(@PathVariable("id") String id) {
        occupationService.delete(id);
        return ResponseObject.success(null);
    }

    @GetMapping("/list")
    @ApiOperation(value = "返回所有岗位", httpMethod = "GET")
    public ResponseObject<List<Occupation>> list() {
        return ResponseObject.success(occupationService.findAll());
    }

    @GetMapping("/page")
    @ApiOperation(value = "查找所有岗位page", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex"),
            @ApiImplicitParam(name = "pageSize")
    })
    public ResponseObject<Pagination<Occupation>> findAllPage(@RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
                                                              @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        return ResponseObject.success(occupationService.findAllPage(pageIndex, pageSize));
    }
}
