package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.FormElementDto;
import com.honeywell.fireiot.dto.TemplateContentDto;
import com.honeywell.fireiot.dto.TemplateDto;
import com.honeywell.fireiot.entity.InspectionContent;
import com.honeywell.fireiot.entity.Template;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @InterfaceName TemplateService
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/9 13:19
 */
public interface TemplateService {

    long save(TemplateDto templateDto);
    void delete(long id);
    long insert(Template template);
    long update(Template template);
    long addContents(FormElementDto data);

    Template isSame(String name);

    Page<Template> findPage();

    Pagination<Template> findPage(Specification<Template> specification);

    void saveContents(TemplateContentDto templateContentDto);

    List<Long> getContentsById(long id);

    void deleteContents(long templateId, long formElementId);


}
