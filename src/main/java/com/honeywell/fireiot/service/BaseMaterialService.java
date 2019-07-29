package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.BaseMaterialDto;
import com.honeywell.fireiot.entity.BaseMaterial;

import java.util.List;


public interface BaseMaterialService extends BaseService<BaseMaterial> {

    BaseMaterial toEntity(BaseMaterialDto dto);

    BaseMaterialDto toDto(BaseMaterial entity);

    List<BaseMaterial> findByCode(String code);

    Long saveBaseMaterial(BaseMaterial baseMaterial);


    boolean checkUpdateBaseMaterial(String code, String oldCode);

    boolean checkDeleteBaseMaterial(Long id);

}
