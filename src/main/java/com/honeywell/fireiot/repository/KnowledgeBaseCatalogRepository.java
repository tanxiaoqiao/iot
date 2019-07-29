package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.KnowledgeBaseCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KnowledgeBaseCatalogRepository extends JpaRepository<KnowledgeBaseCatalog, Long> {
    List<KnowledgeBaseCatalog> findByParentCatalog(KnowledgeBaseCatalog parent);
}
