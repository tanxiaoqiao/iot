package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgeBaseCatalogDto {
    private Long id;

    private String  name;

    private String fullName;

    private String description;

    private Integer sort;

    private Long parentId;

    private List<KnowledgeBaseCatalogDto> children ;

    private List<KnowledgeBaseDocumentDto> documentDtoList;

    private List<Long> addRoleIds;

    private List<Long> editRoleIds;

    private List<Long> deleteRoleIds;


}
