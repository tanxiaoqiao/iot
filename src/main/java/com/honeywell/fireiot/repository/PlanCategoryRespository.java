package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.PlanCategory;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Kayla,Ye
 * @Description:
 * @Date:Created in 3:10 PM 7/19/2018
 */
@Repository
public class PlanCategoryRespository {
    public static final String CATEGORY_ID = "categoryId";
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 添加预案(添加类别)
     *
     * @param
     * @param
     * @return
     */
    public void save(PlanCategory planCategory) {
        mongoTemplate.save (planCategory);
    }


    /**
     * 删除类别
     */
    public boolean removeAreaById(String id) {
        DeleteResult result = mongoTemplate.remove (new Query (Criteria.where (CATEGORY_ID).is (id)),PlanCategory.class);
        return result.wasAcknowledged ();
    }


    /**
     * 更新 预案的类别名字
     */
    public boolean updateCategory(String name,String NewName) {
        Query query = new Query (Criteria.where ("category").is (name));
        Update update = new Update ().set ("category",NewName);
        UpdateResult result = mongoTemplate.updateFirst (query,update,PlanCategory.class);
        return result.isModifiedCountAvailable ();
    }


    /**
     * 根据id查找类别
     *
     * @param categoryId
     * @return
     */
    public PlanCategory findPlanByCategoryId(String categoryId) {
        return mongoTemplate.findOne (new Query (Criteria.where (CATEGORY_ID).is (categoryId)),PlanCategory.class);
    }


    /**
     * 分类名字的唯一性
     *
     * @param
     * @return
     */
    public PlanCategory findPlanByCategory(String Category) {
        return mongoTemplate.findOne (new Query (Criteria.where ("category").is (Category)),PlanCategory.class);
    }


    /**
     * 查找所有的类别
     *
     * @return
     */
    public List <PlanCategory> findAll() {
        return mongoTemplate.findAll (PlanCategory.class);
    }


}
