package com.honeywell.fireiot.utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 9:31 AM 11/28/2018
 */
@Data
public class ExcelData {

    @ApiModelProperty(value = "表头")
    private List<String> title;

    @ApiModelProperty(value = "数据")
    private List<List<Object>> rows;

    @ApiModelProperty(value = "标签页名字")
    private String sheetName = "sheet1";

    @ApiModelProperty(value = "文件名字")
    private String fileName = "temp.xlsx";


}
