package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.KnowledgeBaseCatalog;
import com.honeywell.fireiot.entity.KnowledgeBaseDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface KnowledgeBaseDocumentRepository extends JpaRepository<KnowledgeBaseDocument, Long> , JpaSpecificationExecutor<KnowledgeBaseDocument> {
    List<KnowledgeBaseDocument> findByKnowledgeBaseCatalog(KnowledgeBaseCatalog knowledgeBaseCatalog);
}
