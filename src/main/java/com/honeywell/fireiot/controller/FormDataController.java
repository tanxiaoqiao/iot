package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.entity.FormData;
import com.honeywell.fireiot.service.FormDataService;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 表单数据存取控制
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/28 1:49 PM
 */
@RestController
@Api(tags = "动态表单数据管理")
public class FormDataController {

    @Autowired
    FormDataService formDataService;

    @GetMapping("/api/formData")
    @JpaPage
    @ApiOperation(value = "查询表单数据")
    public ResponseObject<Pagination<FormData>> findPage(@ApiParam(required = true) @RequestParam("formUUID") String formUUID) {
        Pagination<FormData> page = formDataService.findPage(formUUID);
        return ResponseObject.success(page);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/api/formData/{id}")
    public ResponseObject findOne(@PathVariable("id") String id) {
        FormData entity = formDataService.findOne(id);
        if (entity != null) {
            return ResponseObject.success(entity);
        }
        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
    }

    @PostMapping("/api/formData")
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody FormData data) {
        // TODO data校验
        formDataService.save(data);
        return ResponseObject.success("OK");
    }
    @PostMapping("/api/formData2")
    @ApiOperation(value = "保存实体，返回id")
    public ResponseObject insert(@ApiParam @Validated @RequestBody FormData data) {
        // TODO data校验
       FormData record = formDataService.insert(data);
        return ResponseObject.success(record.getId());
    }

    @PatchMapping("/api/formData")
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody FormData data) {
        FormData entity = formDataService.findOne(data.getId());
        if (entity != null) {
            BeanUtils.copyProperties(data, entity);
            formDataService.save(entity);
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/api/formData/{id}")
    public ResponseObject<String> delete(@PathVariable("id") String id) {
        formDataService.delete(id);
        return ResponseObject.success("OK");
    }
}
