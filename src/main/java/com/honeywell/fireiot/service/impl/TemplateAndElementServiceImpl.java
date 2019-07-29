package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.entity.TemplateAndElement;
import com.honeywell.fireiot.repository.TemplateAndElementRepository;
import com.honeywell.fireiot.service.TemplateAndElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName TemplateAndFormServiceImpl
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-03-20 13:33
 */
@Service
public class TemplateAndElementServiceImpl implements TemplateAndElementService {
    @Autowired
    private TemplateAndElementRepository templateAndElementRepository;

    @Override
    public void save(TemplateAndElement templateAndElement) {
        templateAndElementRepository.save(templateAndElement);

    }

    @Override
    public void insert(TemplateAndElement templateAndElement) {
        templateAndElement.setCreateTime(new Date());
        templateAndElementRepository.saveAndFlush(templateAndElement);

    }

    @Override
    public void update(TemplateAndElement templateAndElement) {
        templateAndElement.setUpdateTime(new Date());
        templateAndElementRepository.saveAndFlush(templateAndElement);
    }
    /**
     * 删除单个或多个巡检内容表单
     * @param templateAndElement
     */
    @Override
    public void delete(TemplateAndElement templateAndElement) {
        templateAndElementRepository.delete(templateAndElement);
    }

    @Override
    public void deleteByTemplateIdAndElementId(long templateId, long formElementId) {

        templateAndElementRepository.deleteByTemplateIdAndAndElementId(templateId,formElementId);
    }

    /**
     * 根据模版id，获取相关表单项id
     * @param templateId
     * @return
     */
    @Override
    public List<Long> getElementIds(long templateId) {
        List<Long> ids = templateAndElementRepository.queryElementIdsById(templateId);
        return ids;
    }
}
