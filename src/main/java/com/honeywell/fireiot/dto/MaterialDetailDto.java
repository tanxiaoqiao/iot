package com.honeywell.fireiot.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MaterialDetailDto implements Serializable {

    private Long id;
    private Long supplierId;
    private float validAmount;
    private float unitPrice;
    private float money;
    private Date deadLine;
    private Date stockTime;
    private String description;
    private Long stockMaterialId;

}
