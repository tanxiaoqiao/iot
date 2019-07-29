package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.KnowledgeBaseDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeBaseDocumentOperatingRecordRepository extends JpaRepository<KnowledgeBaseDocument, Long> {
}
