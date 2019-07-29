package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * 校验项dto
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/28 9:46 AM
 */
@Data
public class FormValidatorDto {

//    @ApiModelProperty(name = "是否必须")
    private boolean required;
//    @ApiModelProperty(name = "最大数值")
    private double maxNumber;
//    @ApiModelProperty(name = "最小数值")
    private double minNumber;
//    @ApiModelProperty(name = "最大长度")
    private double maxLength;
//    @ApiModelProperty(name = "最小长度")
    private double minLength;
//    @ApiModelProperty(name = "正则表达式")
    private String pattern;
//    @ApiModelProperty(name = "文件后缀格式")
    private String suffixPattern;
    private String unit;// 单位
}
