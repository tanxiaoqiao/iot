package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dev_model_number_parameter")
public class ModelNumberParameter extends BaseEntity<ModelNumberParameter> {

    private String name;//参数名

    private String  value;//参数值

    private String  unit;//单位

    @ManyToOne
    @JoinColumn(name="model_number_id")
    @JsonIgnore
    private ModelNumber modelNumber;
}
