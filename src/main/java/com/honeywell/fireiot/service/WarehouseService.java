package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.WarehouseDto;
import com.honeywell.fireiot.entity.Warehouse;


public interface WarehouseService extends BaseService<Warehouse> {

    Warehouse toEntity(WarehouseDto dto);

    WarehouseDto toDto(Warehouse entity);
}
