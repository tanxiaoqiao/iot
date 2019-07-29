package com.honeywell.fireiot.service;



import com.honeywell.fireiot.dto.KnowledgeBaseCatalogDto;

import java.util.List;

public interface KnowledgeBaseCatalogService {
    void  save(KnowledgeBaseCatalogDto knowledgeBaseCatalogDto);

    void update(KnowledgeBaseCatalogDto knowledgeBaseCatalogDto);

    KnowledgeBaseCatalogDto findById(Long id);

    List<KnowledgeBaseCatalogDto> findAll();

    void deleteById(Long id);

}
