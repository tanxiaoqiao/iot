package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DocumentOperatingRecordDto {
    private Long id;

    private Long userId;

    private String userName;

    private String operatingType;

    private Date operatingTime;
}
