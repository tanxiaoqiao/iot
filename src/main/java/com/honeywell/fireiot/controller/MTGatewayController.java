package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.fire.MTProtocolParse;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MTPointController
 * @Author YingZhang
 * @Date 12/26/2018 1:19 PM
 **/
@RestController
@RequestMapping("/api/point")
@Api(value = "MTGateway控制", tags = {"网关更新"})
public class MTGatewayController {

    @Autowired
    private MTProtocolParse mtProtocolParse;

    @PostMapping(value = "/restart")
    @ApiOperation(value = "重启网关")
    public ResponseObject restart() {
        try {
            mtProtocolParse.restart();
            return ResponseObject.success(null);
        } catch (Exception e) {
            return ResponseObject.fail(ErrorEnum.PARSE_GATEWAY_ERROR);
        }
    }

}
