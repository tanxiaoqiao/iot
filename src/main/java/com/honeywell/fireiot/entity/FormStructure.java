package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.*;
import java.util.List;

/**
 * 动态表单结构定义
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/11/26 1:28 PM
 */
@Data
@Entity
@Table(name = "dev_form_structure")
@ToString(exclude = {"parent"})
public class FormStructure extends BaseEntity<FormStructure> {

    @Column(unique = true)
    @Indexed
    private String uuid;           // 表单编号
    @Column(nullable = false)
    private String name;           // 表单名称
    @Column(length = 1000)
    private String description;
    private boolean isRoot;        // 是否为根表单
    private int layoutMode = 1;    // 布局模式，1为Element独占一行，2为根据Element中的length进行布局

    @JsonIgnore
    @ManyToOne
    private FormStructure parent;
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<FormStructure> subForms;
    @OneToMany(mappedBy = "form", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FormElement> elements;

}
