package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.dto.PlanFileSearch;
import com.honeywell.fireiot.entity.PlanFile;
import com.honeywell.fireiot.utils.Pagination;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kayla,Ye
 * @Description:
 * @Date:Created in 3:55 PM 7/19/2018
 */
@Repository
public class PlanFileRespository {
    private final static String FILE_ID = "fileId";
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    PlanCategoryRespository planCategoryRespository;

    /**
     * 添加文件
     *
     * @param
     * @param
     * @return
     */
    public void save(PlanFile planFile) {
        mongoTemplate.save (planFile);
    }


    /**
     * 删除文件
     */
    public boolean removeFileById(String fileId) {
        PlanFile planFile = mongoTemplate.findOne (new Query (Criteria.where (FILE_ID).is (fileId)),PlanFile.class);
        File file = new File (planFile.getFilePath ());  //删除文件
        if (file.exists () && file.isFile ()) {
            file.delete ();
        }
        File pic = new File (planFile.getPicturePath ());
        if (pic.exists () && pic.isFile ()) {
            pic.delete ();
        }
        DeleteResult result = mongoTemplate.remove (planFile); //删除数据库的信息
        return result.wasAcknowledged ();
    }

    /**
     * 根据类别Id 删除文件
     *
     * @param categoryId
     * @return
     */
    public boolean removeFileByCategoryId(String categoryId) {

        DeleteResult result = mongoTemplate.remove (new Query (Criteria.where (PlanCategoryRespository.CATEGORY_ID).is (categoryId)),PlanFile.class);
        return result.wasAcknowledged ();
    }


    /**
     * 查找文件
     *
     * @param fileId
     * @return
     */
    public PlanFile findFileById(String fileId) {
        return mongoTemplate.findOne (new Query (Criteria.where (FILE_ID).is (fileId)),PlanFile.class);
    }


    /**
     * 分类名字的唯一性
     *
     * @param
     * @return
     */
    public PlanFile findByfileTitle(String categoryId,String fileTitle) {

        Query query = new Query ();
        Criteria criteria = Criteria.where (PlanCategoryRespository.CATEGORY_ID).is (categoryId)
                .andOperator (Criteria.where ("fileTitle").is (fileTitle));

        query.addCriteria (criteria);
        return mongoTemplate.findOne (query,PlanFile.class);

    }

    /**
     * 查找类别下的所有文件
     */
    public List <PlanFile> findByCategoryId(String categoryId) {
        Query query = new Query ();
        Criteria criteria = Criteria.where (PlanCategoryRespository.CATEGORY_ID).is (categoryId);

        query.addCriteria (criteria);
        return mongoTemplate.find (query,PlanFile.class);
    }

    /**
     * 查找所有的文件
     *
     * @return
     */
    public List <PlanFile> findAll() {
        return mongoTemplate.findAll (PlanFile.class);
    }


    public Pagination <PlanFile> getFiles(PlanFileSearch planFileSearch) {
        Query query = new Query ();
        Criteria criteria = new Criteria ();
        if (planFileSearch.getCategoryId () != null) {
            criteria = Criteria.where ("CategoryId").is (planFileSearch.getCategoryId ());
        }
        if (StringUtils.isNotBlank (planFileSearch.getKeyword ())) {
            criteria = criteria.orOperator (
                    Criteria.where ("fileTitle").regex (planFileSearch.getKeyword ()),
                    Criteria.where ("fileDescription").regex (planFileSearch.getKeyword ()));
        }
        List <PlanFile> list = new ArrayList <> ();

        if (criteria != null) {
            query.addCriteria (criteria);
            list = mongoTemplate.find (query,PlanFile.class);
        } else {
            list = findAll ();
        }
        int count = list.size ();
        return new Pagination <> (count,list);
    }
}
