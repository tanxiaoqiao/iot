package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: Kayla, Ye
 * @Description:  物资与订单关系
 * @Date:Created in 2:14 PM 3/27/2019
 */
@Data
@Entity
@Table(name = "wms_material_and_order")
public class MaterialAndOrder extends BaseEntity<MaterialAndOrder>{

    private Long stockOrderId;

    private Integer materialType; // 当前操作对象 0-库存物资, 1-物资详情

    private Long stockMaterialId;

    private Long materialDetailId;

    @Column(scale = 2)
    private float amount;

    @Column(scale = 2)
    private float money;

    @Column(scale = 2)
    private float backAmount;

}
