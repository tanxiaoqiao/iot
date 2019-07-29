package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.FormElementDto;
import com.honeywell.fireiot.dto.FormStructureDto;
import com.honeywell.fireiot.entity.FormElement;
import com.honeywell.fireiot.entity.FormStructure;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.FormStructureRepository;
import com.honeywell.fireiot.service.FormElementService;
import com.honeywell.fireiot.service.FormStructureService;
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
import java.util.UUID;

@Service
@Transactional
public class FormStructureServiceImpl implements FormStructureService {

    @Autowired
    FormStructureRepository formStructureRep;
    @Autowired
    FormElementService elementService;

    @Override
    public void save(FormStructure entity) {

        // 保存父关系
        if (entity.getParent() != null && entity.getParent().getId() > 0) {
            Optional<FormStructure> parentForm = findById(entity.getParent().getId());
            // 找不到父Form时抛出异常
            if (!parentForm.isPresent()) {
                throw new BusinessException(ErrorEnum.PARENT_FORM_NOT_FOUND);
            };
            entity.setParent(parentForm.get());
        }
        // 生成元素编号
        if (entity.getElements() != null) {
            for (int i = 0; i < entity.getElements().size(); i++) {
                if (entity.getElements().get(i).getUuid() == null) {
                    entity.getElements().get(i).setUuid(UUID.randomUUID().toString());
                }
                entity.getElements().get(i).getValidator().setElement(entity.getElements().get(i));
            }
        }
        formStructureRep.save(entity);
    }

    @Override
    public void delete(FormStructure entity) {

        formStructureRep.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        formStructureRep.deleteById(id);
    }

    @Override
    public Optional<FormStructure> findById(Long id) {

        return formStructureRep.findById(id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<FormStructure> findPage() {
        Page<FormStructure> entityPage = formStructureRep.findAll(JpaUtils.getPageRequest());
        return entityPage;
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<FormStructure> findPage(Specification<FormStructure> specification) {
        Page<FormStructure> entityPage = formStructureRep.findAll(specification, JpaUtils.getPageRequest());
        return entityPage;
    }

    @Override
    public FormStructure findByUUID(String uuid) {
        FormStructure entity = formStructureRep.findByUUID(uuid);
        return entity;
    }

    @Override
    public FormStructure toEntity(FormStructureDto dto) {
        FormStructure entity = new FormStructure();
        BeanUtils.copyProperties(dto, entity);

        // 转换FormElementDto为FormElement
        if (dto.getElements() != null) {
            List<FormElement> formElements = new ArrayList<>();
            for (FormElementDto elementDto : dto.getElements()) {

                FormElement formElement = elementService.toEntity(elementDto);
                formElement.setForm(entity);
                formElements.add(formElement);
            }
            entity.setElements(formElements);
        }
        // 处理subForm
        if (dto.getSubForms() != null) {
            List<FormStructure> subForms = new ArrayList<>();
            for (FormStructureDto subFormDto : dto.getSubForms()) {
                FormStructure subForm = toEntity(subFormDto);
                subForms.add(subForm);
            }
            entity.setSubForms(subForms);
        }

        return entity;
    }

    @Override
    public FormStructureDto toDto(FormStructure entity) {
        FormStructureDto dto = new FormStructureDto();
        BeanUtils.copyProperties(entity, dto);
        // 转换Element为ElementDto
        if (entity.getElements() != null) {
            List<FormElementDto> elementDtos = elementService.toDto(entity.getElements());
            dto.setElements(elementDtos);
        }
        if (entity.getSubForms() != null) {
            List<FormStructureDto> subFormDtos = new ArrayList<>();
            for (FormStructure subForm : entity.getSubForms()) {
                FormStructureDto subFormDto = toDto(subForm);
                subFormDtos.add(subFormDto);
            }
            dto.setSubForms(subFormDtos);
        }
        return dto;
    }
}