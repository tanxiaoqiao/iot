package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.FormStructureDto;
import com.honeywell.fireiot.entity.FormStructure;

public interface FormStructureService extends BaseService<FormStructure> {

    /**
     * 根据UUID查询表单结构
     * @param uuid
     * @return
     */
    FormStructure findByUUID(String uuid);

    FormStructure toEntity(FormStructureDto dto);

    FormStructureDto toDto(FormStructure entity);
}
