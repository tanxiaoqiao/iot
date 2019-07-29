package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.honeywell.fireiot.constant.FormInputType;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

/**
 * 表单元素
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/26 1:28 PM
 */
@Data
@Entity
@Table(name = "dev_form_element")
@ToString(exclude = "form")
public class FormElement extends BaseEntity<FormElement> {

    @Column(nullable = false, unique = true)
    private String uuid = UUID.randomUUID().toString();
    @Column(nullable = false)
    private String key;
    @Length(max = 1000)
    private String value;
    @Column(name = "element_order")
    private Integer order = -1;        // 顺序，默认为-1
    private Integer grid = 0;          // 栅格数，表示元素宽度：0~24
    @Column(nullable = false)
    private FormInputType inputType;   // 表单输入类型

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name = "dev_form_element_option")
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> options;

    @JsonIgnore
    @ManyToOne
    private FormStructure form;        // 所属表单结构

    // 校验相关字段
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FormValidator validator = new FormValidator();   // 校验项

}