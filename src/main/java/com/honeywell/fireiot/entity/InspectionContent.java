package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName InspectionContent
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-03-21 13:47
 */
@Data
@Entity
@Table(name = "wo_inspect_content")
public class InspectionContent {
    /**
     * id，序号做为标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 对应的formData的id
     */
    @Column
    private long formElementId;
    /**
     * 对应页面显示的巡检内容--formData的element中的巡检内容（key）的value
     */
    @Column
    private String name;
    /**
     * 对应页面显示的模式--formData的element中模式（key）的value
     */
    @Column
    private String mode;
    /**
     * 对应页面显示的运行状态--formData的element中运行状态（key）的value
     */
    @Column
    private String status;
    /**
     * 对应页面的结果--formData的element中的上下限或是选项（）的value
     */
    @Column
    private String result;
    /**
     * 对应页面的默认值 -- formData的element中的默认值（key）的value
     */
    @Column
    private String defaultValue;
    /**
     * 单位
     */
    @Column
    private String unit;
    /**
     * 创建时间
     */
    @Column
    private Date createTime;

}
