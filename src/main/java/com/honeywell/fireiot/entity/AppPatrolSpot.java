package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.dto.AppSpotDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppPatrolSpot
 * @author: dexter
 * @create: 2019-03-28 09:45
 * @description:
 **/
@Data
@Document(collection = "appPatrolSpot")
public class AppPatrolSpot {

    @Id
    private String id;

    private Long patrolId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp commitTime;

    private String employeeId;

    /**
     * 点位列表
     *
     */
    private List<AppSpotDTO> spotList;

}
