package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author: create by kris
 * @description:
 * @date:1/8/2019
 */
@Data
@Entity
@Table(name = "wo_holiday")
public class Holiday extends BaseEntity<Holiday> {


    String name;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    Timestamp startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    Timestamp endTime;

}
