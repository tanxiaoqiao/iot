package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 设备知识库关联
 */
@Data
@Entity
@Table(name = "dev_business_device_kb_rel")
@ToString(exclude = {"businessDevice"})
public class BusinessDeviceKnowledgeBaseRel extends BaseEntity<BusinessDeviceKnowledgeBaseRel> {

    private Long  knowledgeId; //知识库文档ID

    private String type ; //关联类型（维修、维保...）

    @ManyToOne
    @JoinColumn(name="business_device_id")
    @JsonIgnore
    private BusinessDevice businessDevice;

}
