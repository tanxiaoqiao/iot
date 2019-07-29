package com.honeywell.fireiot.water.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * @author: xiaomingCao
 * @date: 2019/1/2
 */
@Entity
@Data
@Table(name = "w_event")
public class WaterEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eui;

    private String fieldName;

    private String description;

    /**
     * ADD or DEL
     *
     */
    @JsonIgnore
    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double max;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double min;

    private Double value;

    private Long startTime;

    private Long endTime;

    private String unit;

    private String deviceLabel;
}
