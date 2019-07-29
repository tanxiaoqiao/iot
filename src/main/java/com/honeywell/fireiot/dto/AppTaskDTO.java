package com.honeywell.fireiot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppTaskDTO
 * @author: dexter
 * @create: 2019-04-04 11:37
 * @description: app端上传task dto
 **/
@Data
public class AppTaskDTO {

    private List<AppElementDTO> elementList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp fillTime;

}
