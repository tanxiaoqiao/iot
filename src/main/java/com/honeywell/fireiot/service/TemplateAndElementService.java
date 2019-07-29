package com.honeywell.fireiot.service;

import com.honeywell.fireiot.entity.TemplateAndElement;

import java.util.List;

/**
 * @InterfaceName TemplateAndElementService
 * @Description  巡检模版与巡检内容表单的关联
 * @Author Monica Z
 * @Date 2019-03-20 13:31
 */
public interface TemplateAndElementService {
    void save(TemplateAndElement templateAndElement);
    void insert(TemplateAndElement templateAndElement);
    void update(TemplateAndElement templateAndElement);
    void delete(TemplateAndElement templateAndElement);

    void deleteByTemplateIdAndElementId(long templateId, long formElementId);

    List<Long> getElementIds(long templateId);

}
