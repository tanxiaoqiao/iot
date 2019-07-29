package com.honeywell.fireiot.water.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * 水系统上传数据项(字段)
 *
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Entity
@Data
@Table(
        name = "w_field",
        indexes = {
                @Index(name = "WF_INDEX_NO", columnList = "deviceNo"),
                @Index(name = "WF_INDEX_EUI", columnList = "eui")
        })
@ApiModel
@Validated
public class WaterField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 设备编号
     */
    @ApiModelProperty(hidden = true)
    @NotBlank
    private String deviceNo;

    /**
     * device EUI
     */
    @ApiModelProperty(example = "ffffff10000043a5")
    @NotBlank
    private String eui;

    /**
     * 字段显示名称
     */
    private String title;

    /**
     * 字段名称
     */
    @ApiModelProperty(example = "electric_pressure_alarm")
    @NotBlank
    private String name;

    /**
     * 字段描述详情
     */
    @Column(length = 500)
    private String description;

    /**
     * 单标符号
     */
    @ApiModelProperty(example = "Kpa")
    private String unitSymbol;

    /**
     * 单位名称
     */
    @ApiModelProperty(example = "千帕")
    private String unitName;

    /**
     * 上限
     */
    private double max;

    /**
     * 下限
     */
    private double min;

    /**
     * 是否检查上下限， 为max，min使用标识
     */
    @ApiModelProperty(notes = "0:弃用，1:启用")
    private int checkRange = 0;


    /**
     * 是否检查true false, 0 或 1
     */
    private int checkTrueFalse = 0;

    /**
     * 是否需要进行数据统计，默认1需要，0为不需要
     */
    private int checkStatistics = 1;


}
