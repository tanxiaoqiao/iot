package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 1:32 PM 4/2/2019
 */
@Data
public class OrderMaterialDto {
    private Long stockMaterialId;
    private float amount;
    private float money;
    private List<MaterialDetailDto> materialDetailList;

}
