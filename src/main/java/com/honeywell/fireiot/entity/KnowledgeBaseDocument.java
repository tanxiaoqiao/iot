package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 知识库分类文档
 */
@Data
@Entity
@Table(name = "knowledge_base_document")
public class KnowledgeBaseDocument extends BaseEntity<KnowledgeBaseDocument> {

    private  String  title;

    private String label;

    private String content;

    private String fileName;

    private String filePath;

    private Integer sort;

    @ManyToOne
    @JoinColumn(name="catalog_id")
    @JsonIgnore
    private KnowledgeBaseCatalog knowledgeBaseCatalog;

    @OneToMany(mappedBy = "knowledgeBaseDocument", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<KnowledgeBaseDocumentOperatingRecord> operatingRecordList ;


}
