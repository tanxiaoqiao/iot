package com.honeywell.fireiot.dto;


import com.honeywell.fireiot.constant.MaterialOperateType;
import com.honeywell.fireiot.constant.MaterialOrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StockOrderDto implements Serializable {

    private Long id;
    private MaterialOperateType operationType;
    private String orderNumber;
    private float money;
    private Date time;
    private Long adminId;
    private Long managerId;
    private Long handlerId;
    private Long operatorId;
    private String associatedNumber;
    private String description;
    private MaterialOrderStatusEnum status;
    private Long warehouseId;


}
