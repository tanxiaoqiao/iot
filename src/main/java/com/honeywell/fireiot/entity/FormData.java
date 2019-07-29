package com.honeywell.fireiot.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

/**
 * 动态表单数据存储类
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/3 1:52 PM
 */
@Data
@Document(collection = "formData")
public class FormData {
    @Id
    private String id;

    @Indexed
    private String uuid;           // 表单编号
    private String name;           // 表单名称
    private String description;
    private boolean isRoot;        // 是否为根表单
    private int layoutMode = 1;    // 布局模式，1为Element独占一行，2为根据Element中的length进行布局

    private List<FormStructure> subForms;
    private List<FormElement> elements;
}
