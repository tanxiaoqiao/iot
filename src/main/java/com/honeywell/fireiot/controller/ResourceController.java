package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.dto.ResourceDto;
import com.honeywell.fireiot.service.ResourceService;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/14/2018 9:43 AM
 */
@RestController
@RequestMapping("/api/resource")
@Api(tags = {"资源管理"})
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    @GetMapping
    @ApiOperation(value = "资源列表", httpMethod = "GET")
    public ResponseObject<List<ResourceDto>> findAll() {
        List<ResourceDto> all = resourceService.getAll();
        return ResponseObject.success(all);
    }
}
