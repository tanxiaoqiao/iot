package com.honeywell.fireiot.entity;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 知识库分类
 */
@Data
@Entity
@Table(name = "knowledge_base_catalog")
public class KnowledgeBaseCatalog extends BaseEntity<KnowledgeBaseCatalog> {

    private  String  name;

    private String fullName;

    private String description;

    private Integer sort;

    //上级分类
    @ManyToOne(cascade = { CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @OrderBy(value = "name ASC")
    private KnowledgeBaseCatalog parentCatalog;

    // 子分类列表
    @OneToMany(mappedBy = "parentCatalog", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @OrderBy(value = "name ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<KnowledgeBaseCatalog> childList = new ArrayList<>();

    @OneToMany(mappedBy = "knowledgeBaseCatalog", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<CatalogRoleRel> roleRelList ;

    @OneToMany(mappedBy = "knowledgeBaseCatalog", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<KnowledgeBaseDocument> documentList;
}
