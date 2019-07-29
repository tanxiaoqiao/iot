package com.honeywell.fireiot.repository;



import com.honeywell.fireiot.entity.FormData;
import com.honeywell.fireiot.utils.MongoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 动态表单数据存取
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/3 1:27 PM
 */
@Repository
public class FormDataRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 存储FormData
     * @param data
     */
    public void save(FormData data) {
        mongoTemplate.save(data);
    }

    public FormData insert(FormData data){
        mongoTemplate.insert(data);
        return data;
    }

    public FormData findOne(String id) {
        FormData formData = mongoTemplate.findById(id, FormData.class);
        return formData;
    }

    public List<FormData> findPage(String formUUID) {
        Query pageQuery = MongoUtils.getPageQuery();
        Query query = pageQuery.addCriteria(Criteria.where("uuid").is(formUUID));
        List<FormData> dataList = mongoTemplate.find(query, FormData.class);
        return dataList;
    }

    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(id)), FormData.class);
    }
}
