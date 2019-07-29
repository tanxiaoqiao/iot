package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.entity.FormData;
import com.honeywell.fireiot.repository.FormDataRepository;
import com.honeywell.fireiot.service.FormDataService;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 动态表单数据处理类
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/3 1:38 PM
 */
@Service
@Transactional
public class FormDataServiceImpl implements FormDataService {

    @Autowired
    FormDataRepository formDataRep;

    @Override
    public FormData findOne(String id) {
        FormData data = formDataRep.findOne(id);
        return data;
    }

    @Override
    public Pagination<FormData> findPage(String formUUID) {
        List<FormData> page = formDataRep.findPage(formUUID);
        Pagination<FormData> pagination = new Pagination<>(page.size(), page);
        return pagination;
    }

    @Override
    public void save(FormData data) {
        formDataRep.save(data);
    }

    @Override
    public void delete(String id) {
        formDataRep.delete(id);
    }

    @Override
    public  FormData insert(FormData formData) {
       FormData data =  formDataRep.insert(formData);
        return data;
    }


}
