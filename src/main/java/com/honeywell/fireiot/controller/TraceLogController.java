package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.ExcelTitle;
import com.honeywell.fireiot.dto.TraceLogDto;
import com.honeywell.fireiot.entity.TraceLog;
import com.honeywell.fireiot.service.TraceLogService;
import com.honeywell.fireiot.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/3/14 11:56 AM
 */
@Slf4j
@Api(tags = {"日志查询"})
@RestController
public class TraceLogController {

    @Autowired
    TraceLogService traceLogService;

    @GetMapping("/api/traceLog")
    @JpaPage
    @ApiOperation(value = "分页查询")
    public Object findPage() {
        Page<TraceLog> pageList = traceLogService.findPage(JpaUtils.getSpecification());

        Pagination<TraceLogDto> pagination = new Pagination<TraceLogDto>((int) pageList.getTotalElements(), traceLogService.toDto(pageList.getContent()));
        return ResponseObject.success(pagination);
    }

    @GetMapping("/api/traceLog/excel")
    @JpaPage
    @ApiOperation(value = "按条件查询，不分页，导出Excel使用")
    public Object find(HttpServletResponse response) throws Exception{
       List<TraceLog> results = traceLogService.find(JpaUtils.getSpecification());
        if(results.size()==0){
            return ResponseObject.fail(ErrorEnum.LOG_NOT_EXIST);
        }
        String fileName = UUID.randomUUID() + ".xlsx";
        //构造excelData
        ExcelData data = new ExcelData();
        data.setFileName(fileName);
        data.setTitle(ExcelTitle.LOG_TITLE_LIST);
        List<List<Object>> rowData = new ArrayList<>();
        for(TraceLog traceLog :results){
            List<Object> row = new ArrayList<>();
            row.add(traceLog.getType().getDescription());
            row.add(traceLog.getContent());
            row.add(traceLog.getOperator());
            row.add(traceLog.getOperateTime());
            rowData.add(row);
        }
        data.setRows(rowData);

        ExcelUtil.exportExcel(response, data);

        return ResponseObject.success(null);
    }
}
