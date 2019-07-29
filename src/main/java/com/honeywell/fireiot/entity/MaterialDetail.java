package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: Kayla, Ye
 * @Description: 物资详情（物资入库==新增库存物资）
 * @Date:Created in 2:03 PM 3/27/2019
 */
@Data
@Entity
@Table(name = "wms_material_detail")
public class MaterialDetail extends  BaseEntity<MaterialDetail>{

    @NotNull(message = "supplier_Id_not_null")
    private Long supplierId;//供应商

    @NotNull(message = "valid_amount_not_null")
    @Column(name = "valid_amount", scale =  2)
    private float validAmount;//有效数量，账面数量

    @NotNull(message = "valid_amount_not_null")
    @Column(name = "unit_price", scale =  2)
    private float unitPrice;//单价

    @NotNull(message = "valid_amount_not_null")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date deadLine;//过期时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date stockTime;//入库时间

    private String description;

    private Long stockMaterialId;//跟对应物资

    private Integer status = 0; //物资状态 0——有效， 1——失效物资


}
