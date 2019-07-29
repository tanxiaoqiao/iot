package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 1:41 PM 3/29/2019
 */
@Data
public class StockOrderMaterialDto {

    private StockOrderDto stockOrderDto;

    private List<OrderMaterialDto> operateMaterial;

    private Long targetWarehouseId;//目标仓库ID，用于移库使用

    private Long targetAdminId; //目标仓库管理者，用于移库使用
}
