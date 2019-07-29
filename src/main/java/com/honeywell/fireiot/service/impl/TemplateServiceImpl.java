package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.dto.FormElementDto;
import com.honeywell.fireiot.dto.TemplateContentDto;
import com.honeywell.fireiot.dto.TemplateDto;
import com.honeywell.fireiot.entity.FormElement;
import com.honeywell.fireiot.entity.InspectionContent;
import com.honeywell.fireiot.entity.Template;
import com.honeywell.fireiot.entity.TemplateAndElement;
import com.honeywell.fireiot.repository.TemplateRepository;
import com.honeywell.fireiot.service.*;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName TemplateServiceImpl
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/9 13:19
 */
@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private FormElementService formElementService;
    @Autowired
    private FormStructureService formStructureService;
    @Autowired
    private TemplateAndElementService templateAndElementService;
    @Autowired
    private AsynFormElementService asynFormElementService;
    @Autowired
    private InspectContentService inspectContentService;

    @Override
    public long save(TemplateDto templateDto) {
        if (templateDto.getId() == 0){
           Template template =  this.dtoToEntity(templateDto);
           long id =  this.insert(template);
           return id;
        }else {
            Template template = this.dtoToEntity(templateDto);
            Long  id  = this.update(template);
            return id;
        }
    }
    @Override
    public void delete(long id) {
        templateRepository.deleteById(id);
    }

    @Override
    public long insert(Template template) {
       Template record =  this.isSame(template.getName());
       if(record != null){
           return 0;
       }else {
           template.setCreateTime(new Date());
           Template newRecord  = templateRepository.save(template);
           return newRecord.getId();
       }
    }

    @Override
    public long update(Template template) {
        template.setUpdateTime(new Date());
        Template updateRecord = templateRepository.saveAndFlush(template);
        return updateRecord.getId();
    }

    /**
     * 是否存在相同名称的模版
     * @param name
     * @return
     */
    @Override
    public Template isSame(String name) {
       Template record =  templateRepository.findAllByName(name);
        return record;
    }

    @Override
    public Page<Template> findPage() {
        return null;
    }

    @Override
    public Pagination<Template> findPage(Specification<Template> specification) {
        Page<Template> page = templateRepository.findAll(specification, JpaUtils.getPageRequest());
        return new Pagination<>((int) page.getTotalElements(), page.getContent());

    }

    /**
     * 模版内新增巡检内容
     * @param templateContentDto
     */
    @Override
    public void saveContents(TemplateContentDto templateContentDto) {

        FormElementDto elementDto = templateContentDto.getFormElementDto();

        /**
         *  巡检内容对应表单项 --element
         */
        long id = addContents(elementDto);
//        Optional<FormStructure> formOp = formStructureService.findById(elementDto.getFormId());
//        formOp.orElseThrow(() -> new BusinessException(ErrorEnum.ELEMENT_FORM_NOT_FOUND));
//
        FormElement entity = formElementService.toEntity(elementDto);
//        entity.setUuid(UUID.randomUUID().toString());
//        entity.setForm(formOp.get());
//        long id =  formElementService.insert(entity);
        TemplateAndElement tf = new TemplateAndElement();
        tf.setTemplateId(templateContentDto.getId());
        tf.setElementId(id);
        templateAndElementService.insert(tf);

        // 异步处理数据分析入库
//        entity.setId(id);
//        asynFormElementService.toInspectContent(entity);


    }

    /**
     * 获取模版内的巡检内容列表
     * @param id
     * @return
     */
    @Override
    public List<Long> getContentsById(long id) {
        List<Long> ids = templateAndElementService.getElementIds(id);
        return ids;
    }

    @Override
    public void deleteContents(long templateId, long formElementId) {
        templateAndElementService.deleteByTemplateIdAndElementId(templateId, formElementId);
        formElementService.deleteById(formElementId);
        inspectContentService.deleteByElementId(formElementId);
    }


    private  Template dtoToEntity(TemplateDto templateDto){
        Template template = new Template();
        if(templateDto.getId() != 0){
            template.setId(templateDto.getId());
        }
        if(templateDto.getName()!= null){
            template.setName(templateDto.getName());
        }
        if(templateDto.getComments() != null){
            template.setComments(templateDto.getComments());
        }
        if(templateDto.getType()!= null){
            template.setType(templateDto.getType());
        }
       return template;

    }

    /**
     * 添加巡检内容
     * @param formElementDto
     * @return
     */
    @Override
    public long addContents(FormElementDto formElementDto){
        /**
         *  巡检内容对应表单项 --element
         */


        FormElement entity = formElementService.toEntity(formElementDto);
        entity.setUuid(UUID.randomUUID().toString());

        long id =  formElementService.insert(entity);

        // 异步处理数据分析入库
        entity.setId(id);
        asynFormElementService.toInspectContent(entity);
        return id;
    }
}
