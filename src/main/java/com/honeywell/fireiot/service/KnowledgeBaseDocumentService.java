package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.KnowledgeBaseDocumentDto;
import com.honeywell.fireiot.entity.KnowledgeBaseDocument;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface KnowledgeBaseDocumentService {

    void save(KnowledgeBaseDocumentDto knowledgeBaseDocumentDto);

    KnowledgeBaseDocumentDto findById(Long id);

    boolean update(KnowledgeBaseDocumentDto knowledgeBaseDocumentDto);

    void deleteById(Long id);

    void deleteById(Long[] ids);

    List<KnowledgeBaseDocumentDto> findByCatalogId(Long id);

    Pagination<KnowledgeBaseDocumentDto> findPage(Specification<KnowledgeBaseDocument> specification);
}
