package com.honeywell.fireiot.service;


import com.honeywell.fireiot.entity.FormData;
import com.honeywell.fireiot.utils.Pagination;

public interface FormDataService {

    FormData findOne(String id);

    Pagination<FormData> findPage(String formUUID);

    void save(FormData data);

    void delete(String id);

    FormData insert(FormData data);

}
