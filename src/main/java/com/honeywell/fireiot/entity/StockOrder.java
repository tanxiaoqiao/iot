package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.constant.MaterialOperateType;
import com.honeywell.fireiot.constant.MaterialOrderStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: Kayla, Ye
 * @Description: 仓库存储单
 * @Date:Created in 2:01 PM 3/27/2019
 */
@Data
@Entity
@Table(name = "wms_stock_order")
public class StockOrder extends BaseEntity<StockOrder> {

    @Enumerated(EnumType.ORDINAL)
    private MaterialOperateType operationType; //操作类型

    private String orderNumber;//单号

    @Column(scale = 2)
    private float money;//金额

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date time;//表单时间

    private Long adminId;//保管员

    private Long managerId;//主管

    private Long  handlerId;//处理人

    private Long operatorId;//操作人

    private String associatedNumber;//关联单号

    private String description;

    @Enumerated(EnumType.ORDINAL)
    private MaterialOrderStatusEnum status ;//订单状态预定状态

    private Long warehouseId;//仓库id


}
