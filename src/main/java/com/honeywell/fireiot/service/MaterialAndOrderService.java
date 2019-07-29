package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.MaterialDetailDto;
import com.honeywell.fireiot.dto.OrderMaterialDto;
import com.honeywell.fireiot.entity.MaterialAndOrder;
import com.honeywell.fireiot.entity.StockOrder;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.jpa.domain.Specification;


public interface MaterialAndOrderService extends BaseService<MaterialAndOrder> {
    void saveOrderMaterial(OrderMaterialDto orderMaterialDto, StockOrder stockOrder);

    void saveOrderMaterialDetail(MaterialDetailDto materialDetailDto, StockOrder stockOrder);

    void update(MaterialAndOrder materialAndOrder);

    Pagination<MaterialAndOrder> findMaterialPage(Specification<MaterialAndOrder> var1);
}
