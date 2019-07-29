package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @Author: Kayla, Ye
 * @Description:物资信息
 * @Date:Created in 1:59 PM 3/27/2019
 */
@Data
@Entity
@Table(name = "wms_stock_material")
public class StockMaterial extends BaseEntity<StockMaterial> {

    private Long  warehouseId;//所属仓库

    @NotNull(message = "warehouse_id_not_null")
    private Long  baseMaterialId;//所属基础物资

    private String shelf;//货架

    @Column(name = "valid_amount", scale =  2)
    private float validAmount;//有效数量，账面数量

    @Column(name = "lock_amount",scale = 2)
    private float lockAmount;//锁定数量

    @Column(name = "min_amount",scale = 2)
    private float minAmount;

    private Integer status = 0; //物资状态 0——有效， 1——失效物资




}
