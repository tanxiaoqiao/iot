package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgeBaseDocumentDto {
    private Long id;

    private String  title;

    private String label;

    private String content;

    private String fileName;

    private String filePath;

    private Integer sort;

    private Long catalogId;

    private Long userId;

    private String userName;

    List<DocumentOperatingRecordDto> operatingRecordDtoList;

}
