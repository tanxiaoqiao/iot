package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "knowledge_base_operating_record")
public class KnowledgeBaseDocumentOperatingRecord extends BaseEntity<KnowledgeBaseDocumentOperatingRecord>{

    private Long userId;

    private String userName;

    private String operatingType;

    private Date operatingTime;

    @ManyToOne
    @JoinColumn(name="document_id")
    @JsonIgnore
    private KnowledgeBaseDocument knowledgeBaseDocument;

}
