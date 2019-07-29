package com.honeywell.fireiot.dto;



import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/28 9:46 AM
 */
@Data
public class FormElementDto implements Serializable {

//    @ApiModelProperty(value = "主键")
    private Long id;
//    @ApiModelProperty(value = "编号")
    private String uuid;
//    @ApiModelProperty(value = "元素名称")
    private String key;
//    @ApiModelProperty(value = "元素值")
    private String value;
//    @ApiModelProperty(value = "元素顺序")
    private Integer order;
//    @ApiModelProperty(value = "栅格数，表示元素宽度，用于编辑页面input宽度设定：0~24")
    private Integer grid;
//    @ApiModelProperty(value = "元素输入框类型")
    private String inputType;
//    @ApiModelProperty(value = "Class类型"
    private String ClassType;
    private Map<String, String> options;

//    @ApiModelProperty(value = "校验项")
    private FormValidatorDto validator = new FormValidatorDto();

//    @ApiModelProperty(value = "表单ID")
    private Long formId;
}
