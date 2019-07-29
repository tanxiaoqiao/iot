package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.PlanFileSearch;
import com.honeywell.fireiot.entity.PlanCategory;
import com.honeywell.fireiot.entity.PlanFile;
import com.honeywell.fireiot.service.PlanCategoryService;
import com.honeywell.fireiot.service.PlanFileService;
import com.honeywell.fireiot.utils.FileUtil;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @Author: Kayla,Ye
 * @Description:
 * @Date:Created in 11:05 AM 8/2/2018
 */
@RestController
@RequestMapping("/plan/ops")
@Api(tags = "预案接口")
public class PlanController {

    private static final Logger LOGGER = LoggerFactory.getLogger (PlanController.class);
    private final static String PLAN_DIR = "./plandata/";
    @Autowired
    PlanCategoryService planCategoryService;
    @Autowired
    PlanFileService planFileService;

    //分类操作
    @PostMapping("/addCategory")
    @ApiOperation(value = "增加分类") //方法描述
    @ApiImplicitParam(name = "planCategory", value = "类别名称(此时id可不需要传入)", required = true, dataType = "PlanCategory")
    public ResponseObject addCategory(@Validated @RequestBody PlanCategory planCategory) {

        LOGGER.info ("addCategory|planCategory:{} ",planCategory);
        //验证唯一类别名字
        if (planCategoryService.checkUniqueCategory (planCategory.getCategory ())) {
            return ResponseObject.fail (ErrorEnum.CATEGORY_EXIST);
        }
        planCategoryService.add (planCategory);
        return ResponseObject.success (planCategory.getCategoryId ());
    }

    @PostMapping("/deleteCategory")
    @ApiOperation(value = "删除分类") //方法描述
    @ApiImplicitParam(name = "data", value = "传入关键字为categoryIds的json字符串数组", required = true)
    public ResponseObject deleteCategory(@RequestBody Map <String, Object> data) {
        LOGGER.info ("deleteCategory|data:{} ",data);

        ArrayList <String> categoryIds = (ArrayList <String>) data.get ("categoryIds");
        if ((categoryIds.isEmpty ())) {
            return ResponseObject.fail (ErrorEnum.MISS_REQUEST_PARAMTER);
        }

        for (String id : categoryIds) {
            //id号是否存在
            if (null == planCategoryService.findCategoryById (id)) {
                return ResponseObject.fail (ErrorEnum.CATEGORY_NOT_EXIST);
            }
            planCategoryService.deleteCategoryById (id);
        }
        return ResponseObject.success (null);
    }

    @PostMapping("/updateCategory")
    @ApiOperation(value = "修改分类名字")
    @ApiImplicitParam(name = "planCategory", value = "类别", required = true, dataType = "PlanCategory")
    public ResponseObject updateCategory(@Validated @RequestBody PlanCategory planCategory) {
        LOGGER.info ("updateCategory|planCategory:{}",planCategory);

        //判断该类别是否存在
        if (null == planCategoryService.findCategoryById (planCategory.getCategoryId ())) {
            return ResponseObject.fail (ErrorEnum.CATEGORY_NOT_EXIST);
        }

        planCategoryService.add (planCategory);

        return ResponseObject.success (null);

    }

    @GetMapping("/listCategory")
    @ApiOperation(value = "查找所有类别")
    public ResponseObject listCategory() {

        List <PlanCategory> planCategoryList = planCategoryService.findAll ();
        Pagination <PlanCategory> planCategoryPagination = new Pagination <> ();
        planCategoryPagination.setTotalCount (planCategoryList.size ());
        planCategoryPagination.setDataList (planCategoryList);
        return ResponseObject.success (planCategoryPagination);
    }


    //文件操作

    @PostMapping("/uploadDevices")
    @ApiOperation(value = "导入文件")
    public ResponseObject uploadDevices(@RequestParam("file") MultipartFile file,HttpServletRequest request) throws Exception {
        if (file.isEmpty ()) {
            return ResponseObject.fail (ErrorEnum.IMPORT_DATA_FAIL);
        }

        DateFormat df = new SimpleDateFormat ("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance ();
        String fileName = df.format (calendar.getTime ()) + file.getOriginalFilename ();

        String filePath = PLAN_DIR;
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

    @PostMapping("/addFile")
    @ApiOperation(value = "增加文件") //方法描述
    @ApiImplicitParam(name = "planFile", value = "类别id为必传项，文件id此时可去除)", required = true, dataType = "PlanFile")
    public ResponseObject addFile(@Validated @RequestBody PlanFile planFile) {

        LOGGER.info ("addFile|planFile:{} ",planFile);
        //验证类别是否存在
        if (null == planCategoryService.findCategoryById (planFile.getCategoryId ())) {
            return ResponseObject.fail (ErrorEnum.CATEGORY_NOT_EXIST); //类别不存在
        }

        //判断文件题目是否重复
        if (planFileService.checkUniqueFile (planFile.getCategoryId (),planFile.getFileTitle ())) {
            return ResponseObject.fail (ErrorEnum.FILETITLE_EXIST);
        }

        planFile.setFilePath (PLAN_DIR + planFile.getFilePath ());
        planFile.setPicturePath (PLAN_DIR + planFile.getPicturePath ());
        planFileService.add (planFile);
        return ResponseObject.success (planFile.getFileId ());
    }


    @PostMapping("/deleteFile")
    @ApiOperation(value = "删除文件") //方法描述
    @ApiImplicitParam(name = "data", value = "关键字为fileIds的json字符串数组", required = true)
    public ResponseObject deleteFile(@RequestBody Map <String, Object> data) {
        LOGGER.info ("deleteFile|data:{} ",data);

        ArrayList <String> fileIds = (ArrayList <String>) data.get ("fileIds");
        if ((fileIds.isEmpty ())) {
            return ResponseObject.fail (ErrorEnum.MISS_REQUEST_PARAMTER);
        }

        for (String id : fileIds) {
            //id号是否存在
            if (null == planFileService.findFileById (id)) {
                return ResponseObject.fail (ErrorEnum.FILE_NOT_EXIST);
            }
            planFileService.deleteFileById (id);
        }
        return ResponseObject.success (null);
    }

    @PostMapping("/updateFile")
    @ApiOperation(value = "修改文件")
    @ApiImplicitParam(name = "planFile", value = "文件(类别id以及文件id为必传项)", required = true, dataType = "PlanFile")
    public ResponseObject updateFile(@Validated @RequestBody PlanFile planFile) {
        LOGGER.info ("updateFile|planFile:{}",planFile);

        //验证类别是否存在
        if (null == planCategoryService.findCategoryById (planFile.getCategoryId ())) {
            return ResponseObject.fail (ErrorEnum.CATEGORY_NOT_EXIST); //类别不存在
        }

        if (null == planFileService.findFileById (planFile.getFileId ())) {
            return ResponseObject.fail (ErrorEnum.FILE_NOT_EXIST);//文件不存在
        }
        //判断文件题目是否重复
        if (planFileService.checkUniqueFile (planFile.getCategoryId (),planFile.getFileTitle ())) {
            return ResponseObject.fail (ErrorEnum.FILETITLE_EXIST);
        }

        planFileService.add (planFile);
        return ResponseObject.success (null);

    }

    @GetMapping("/listFile")
    @ApiOperation(value = "文件查找")
    public ResponseObject listFile(PlanFileSearch planFileSearch) {

        LOGGER.info ("listFile|planFileSearch:{}",planFileSearch);
        if (null == planFileSearch) {
            return ResponseObject.fail (ErrorEnum.PARAMS_ERROR);
        }

        Pagination <PlanFile> planFilePagination = planFileService.getFile (planFileSearch);

        return ResponseObject.success (planFilePagination);
    }

}