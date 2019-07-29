package com.honeywell.fireiot.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class StockMaterialDto implements Serializable {

    private Long id;
    private Long warehouseId;
    private Long baseMaterialId;
    private String shelf;
    private float validAmount;
    private float lockAmount;
    private float minAmount;
    private Integer status;

}
