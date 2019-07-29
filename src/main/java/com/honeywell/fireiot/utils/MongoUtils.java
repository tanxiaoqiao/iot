package com.honeywell.fireiot.utils;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Map;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/3 2:15 PM
 */
public class MongoUtils {

    /**
     * 获取分页的Query对象
     *
     * @return
     */
    public static Query getPageQuery() {
        Integer pi = PageHolder.getHolder().getPi();
        Integer ps = PageHolder.getHolder().getPs();
        Map<String, Object> pageParameter = PageHolder.getHolder().getPageParameter();

        Query query = new Query().skip(pi * ps).limit(ps);
        Criteria criteria = new Criteria();

        if (pageParameter != null) {
            /**
             * 连接查询条件, 不定参数，可以连接0..N个查询条件
             */
            for (Map.Entry<String, Object> entry : pageParameter.entrySet()) {
                criteria.and(entry.getKey()).equals(entry.getValue());
            }
        }
        query = query.addCriteria(criteria);
        return query;
    }
}
