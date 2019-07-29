package com.honeywell.fireiot.service;





import com.honeywell.fireiot.dto.FormElementDto;
import com.honeywell.fireiot.entity.FormElement;

import java.util.List;

public interface FormElementService extends BaseService<FormElement> {


    FormElement toEntity(FormElementDto dto);

    FormElementDto toDto(FormElement entity);

    List<FormElementDto> toDto(List<FormElement> elements);

    long insert(FormElement entity);
}
