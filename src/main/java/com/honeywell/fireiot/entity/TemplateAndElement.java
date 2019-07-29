package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName TemplateAndElement
 * @Description 巡检模版所包含的巡检内容,对应巡检表单项
 * @Author Monica Z
 * @Date 2019-03-20 13:22
 */

@Table(name = "wo_template_and_form")
@Entity
@Data
public class TemplateAndElement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long templateId;
    @Column
    private long elementId;
    @Column
    private Date createTime;
    @Column
    private Date updateTime;
}
