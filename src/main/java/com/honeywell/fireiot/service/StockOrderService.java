package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.StockOrderDto;
import com.honeywell.fireiot.dto.StockOrderMaterialDto;
import com.honeywell.fireiot.entity.StockOrder;


public interface StockOrderService extends BaseService<StockOrder> {

    StockOrder toEntity(StockOrderDto var1);

    StockOrderDto toDto(StockOrder var1);

    String stockOperate(StockOrderMaterialDto var1);
}
