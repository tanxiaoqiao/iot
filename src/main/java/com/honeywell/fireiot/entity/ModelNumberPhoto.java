package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dev_model_number_photo")
public class ModelNumberPhoto extends BaseEntity<ModelNumberPhoto>{

    private String photoPath;//图片地址

    @ManyToOne
    @JoinColumn(name="model_number_id")
    @JsonIgnore
    private ModelNumber modelNumber;


}
