package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 知识库分类角色关联
 */
@Data
@Entity
@Table(name = "knowledge_catalog_role_rel")
public class CatalogRoleRel extends BaseEntity<CatalogRoleRel> {

    private Long roleId;

    private String permissionType;

    @ManyToOne
    @JoinColumn(name="catalog_id")
    @JsonIgnore
    private KnowledgeBaseCatalog knowledgeBaseCatalog;
}
