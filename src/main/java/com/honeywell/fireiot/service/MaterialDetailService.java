package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.MaterialDetailDto;
import com.honeywell.fireiot.entity.MaterialDetail;


public interface MaterialDetailService extends BaseService<MaterialDetail> {
    MaterialDetail toEntity(MaterialDetailDto var1);

    MaterialDetailDto toDto(MaterialDetail var1);

    Long saveDto(MaterialDetailDto var1);

    boolean updateDto(MaterialDetailDto var1);

    void deleteByStockMaterialId(Long var1);

    boolean update(MaterialDetail materialDetail);
}
