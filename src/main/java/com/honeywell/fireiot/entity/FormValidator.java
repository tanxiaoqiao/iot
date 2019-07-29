package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 表单校验项
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/26 1:28 PM
 */
@Data
@Entity
@Table(name = "dev_form_validator")
@ToString(exclude = "element")
public class FormValidator extends BaseEntity<FormValidator> {

    // 校验相关字段
    private boolean required;     // 是否为必填
    private double maxNumber;    // 最大数值
    private double minNumber;    // 最小数值
    private double maxLength;    // 最大输入长度
    private double minLength;    // 最小输入长度
    private String pattern;       // 正则表达式，用于输入校验
    private String suffixPattern; // 文件后缀格式
    private String unit;// 单位


    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "validator")
    private FormElement element;

}
