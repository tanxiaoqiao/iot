package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.constant.FormInputType;
import com.honeywell.fireiot.dto.FormElementDto;
import com.honeywell.fireiot.dto.FormValidatorDto;
import com.honeywell.fireiot.entity.FormElement;
import com.honeywell.fireiot.repository.FormElementRepository;
import com.honeywell.fireiot.service.FormElementService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FormElementServiceImpl implements FormElementService {

    @Autowired
    FormElementRepository formElementRep;

    @Override
    public void save(FormElement entity) {
        formElementRep.save(entity);
    }

    @Override
    public void delete(FormElement entity) {
        formElementRep.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        formElementRep.deleteById(id);
    }

    @Override
    public Optional<FormElement> findById(Long id) {
        return formElementRep.findById(id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<FormElement> findPage() {
        Page<FormElement> entityPage = formElementRep.findAll(JpaUtils.getPageRequest());
        return entityPage;
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<FormElement> findPage(Specification<FormElement> specification) {
        Page<FormElement> entityPage = formElementRep.findAll(specification, JpaUtils.getPageRequest());
        return entityPage;
    }

    @Override
    public FormElement toEntity(FormElementDto dto) {
        FormElement entity = new FormElement();
        BeanUtils.copyProperties(dto, entity, "");
        // 转换校验项
        if (dto.getValidator() != null) {
            // TODO
            BeanUtils.copyProperties(dto.getValidator(), entity.getValidator());
        }
        if (dto.getInputType() != null) {
            entity.setInputType(FormInputType.valueOf(dto.getInputType()));
        }
        return entity;
    }

    @Override
    public FormElementDto toDto(FormElement entity) {
        FormElementDto dto = new FormElementDto();
        BeanUtils.copyProperties(entity, dto);
        // 转换校验项
        FormValidatorDto validatorDto = new FormValidatorDto();
        BeanUtils.copyProperties(entity.getValidator(), validatorDto);
        dto.setValidator(validatorDto);
        dto.setInputType(entity.getInputType().toString());
        return dto;
    }

    @Override
    public List<FormElementDto> toDto(List<FormElement> elements) {
        if (elements == null) {
            return null;
        }
        List<FormElementDto> elementDtos = new ArrayList<>();
        for (FormElement element : elements) {
            FormElementDto formElementDto = toDto(element);
            elementDtos.add(formElementDto);
        }
        return elementDtos;
    }

    @Override
    public long insert(FormElement entity) {
        FormElement element =  formElementRep.save(entity);
        return element.getId();
    }
}
