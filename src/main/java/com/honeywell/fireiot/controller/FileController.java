package com.honeywell.fireiot.controller;



import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.utils.FileUtil;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Author: Kayla,Ye
 * @Description:
 * @Date:Created in 9:32 AM 10/10/2018
 */
@RestController
@RequestMapping("/file/")
@Api(tags = {"文件管理"})
public class FileController {

    @Value("${com.honeywell.file.path}")
    private String planDir;

    @PostMapping("/upload")
    @ApiOperation(value = "导入文件")
    public ResponseObject uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        if (file.isEmpty ()) {
            return ResponseObject.fail (ErrorEnum.IMPORT_DATA_FAIL);
        }

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance ();
        String fileName = df.format (calendar.getTime ()) + file.getOriginalFilename ();

        String filePath = planDir;
        String dest = null;
        try {
            dest = FileUtil.uploadFile (file.getBytes (),filePath,fileName);
            //  file.transferTo ( dest );
        } catch (IOException e) {
            e.printStackTrace ();
            return ResponseObject.fail (ErrorEnum.IMPORT_DATA_FAIL);
        }

        return ResponseObject.success (fileName);

    }


}
