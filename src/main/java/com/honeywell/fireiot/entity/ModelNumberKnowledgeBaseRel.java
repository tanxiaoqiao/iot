package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 设备型号知识库关联
 */
@Data
@Entity
@Table(name = "dev_model_number_kb_rel")
public class ModelNumberKnowledgeBaseRel extends BaseEntity<ModelNumberKnowledgeBaseRel> {

    private Long  knowledgeId; //知识库文档ID

    private String type ; //关联类型（维修、维保...）

    @ManyToOne
    @JoinColumn(name="model_number_id")
    @JsonIgnore
    private ModelNumber modelNumber;

}
