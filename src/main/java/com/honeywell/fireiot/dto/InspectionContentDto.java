package com.honeywell.fireiot.dto;

/**
 * @ClassName InspectionContentDto
 * @Description 提供可视化的巡检内容列表
 * @Author Monica Z
 * @Date 2019-03-21 13:34
 */
public class InspectionContentDto {
    /**
     * 对应的formData的id
     */
    private String formId;

    /**
     * 对应页面显示的巡检内容--formData的element中的巡检内容（key）的value
     */
    private String name;
    /**
     * 对应页面显示的模式--formData的element中模式（key）的value
     */
    private String mode;
    /**
     * 对应页面显示的运行状态--formData的element中运行状态（key）的value
     */

    private String status;

    /**
     * 对应页面的结果--formData的element中的上下限或是选项（）的value
     */
    private String result;
    /**
     * 对应页面的默认值 -- formData的element中的默认值（key）的value
     */
    private String defaultValue;

}
