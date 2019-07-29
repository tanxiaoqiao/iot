package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.service.FileUploadService;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:05 PM 4/24/2019
 */
@RestController
@RequestMapping("/upload/")
@Api(tags = {"文件上传"})
@Slf4j
public class FileUploadController {

    @Autowired
    FileUploadService fileUploadService;

    @PostMapping("/file")
    @ApiOperation(value = "导入文件")
    public ResponseObject uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String domain, @RequestParam String service) {
        if (file.isEmpty() || StringUtils.isBlank(domain) || StringUtils.isBlank(service)) {
            return ResponseObject.fail(ErrorEnum.PARAS_ERROR);
        }

        String path = domain + File.separator + service + File.separator + file.getOriginalFilename();
        Optional<String> optional = null;
        try {
            optional = fileUploadService.uploadFile(file.getInputStream(), path);

            if (optional.isPresent()) {
                return ResponseObject.success(optional.get());
            } else {
                return ResponseObject.fail(ErrorEnum.UPLOAD_ERROR);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseObject.fail(ErrorEnum.UPLOAD_ERROR);
        }
    }

}
