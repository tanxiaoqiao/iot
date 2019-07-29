package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/**
 * 设备型号
 */
@Data
@Entity
@Table(name = "dev_model_number")
@ToString(exclude = {"deviceType"})
public class ModelNumber extends BaseEntity<ModelNumber>{
    // 型号名称
    private String name;

    @ManyToOne
    @JoinColumn(name="device_type_id")
    @JsonIgnore
    private DeviceType deviceType;

    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="modelNumber" ,orphanRemoval = true)
    private List<ModelNumberKnowledgeBaseRel> modelNumberKnowledgeBaseRel;//型号对应的维修维保知识库文档


    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="modelNumber" ,orphanRemoval = true)
    private List<ModelNumberParameter> modelNumberParameters;//型号对应的参数

    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="modelNumber" ,orphanRemoval = true)
    private List<ModelNumberPhoto> photos;//设备图片

    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="modelNumber" ,orphanRemoval = true)
    private List<ModelNumberFile> files;//设备附件

}
