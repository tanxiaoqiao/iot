package com.honeywell.fireiot.controller;


import com.github.qcloudsms.httpclient.HTTPException;
import com.honeywell.fireiot.service.MobMsgService;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/mobMsg")
@Api(tags = "短信推送消息")
public class MobMsgController {


    @Autowired
    MobMsgService mobMsgService;

    @PostMapping("/single")
    @ApiOperation(value = "发送单条短信", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "message",value = "message"),
            @ApiImplicitParam(name = "phonenumber",value = "phonenumber")
    })
    public ResponseObject<String> sendSingleMsg(@RequestParam("message") String message,
                                                @RequestParam("phonenumber") String phonenumber) throws HTTPException {
        String success = mobMsgService.sendSingleSms(message, phonenumber);
        return ResponseObject.success(success);
    }


    @PostMapping("/multi")
    @ApiOperation(value = "发送多条短信", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "message",value = "message"),
            @ApiImplicitParam(name = "phonenumber",value = "phonenumber")
    })
    public ResponseObject<String> sendMultiMsg(@RequestParam("message") String message,
                                               @RequestParam("phonenumber") List<String> phonenumber) throws HTTPException {
        String[] strings = phonenumber.toArray(new String[phonenumber.size()]);
        String success = mobMsgService.sendMultiSms(message, strings);
        return ResponseObject.success(success);
    }
}

