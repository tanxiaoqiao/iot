package com.honeywell.fireiot.dao.impl;


import com.honeywell.fireiot.dao.PatrolDao;
import com.honeywell.fireiot.dto.PatrolCondition;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

/**
 * @author: create by kris
 * @description:
 * @date:1/8/2019
 */
@Repository
@Transactional(rollbackOn = Exception.class)
public class PatrolDaoImpl implements PatrolDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Pagination<Patrol> findByCondition(PatrolCondition condition) {
        String sql = "from Patrol pt where 1 = 1";

        if (condition.getStatus() != null) {
            sql += " and pt.status = " + condition.getStatus();
        }
        if (condition.getPreStartTime() != null) {
            sql += " and pt.preStartTime >= '" + condition.getPreStartTime() + "'";
        }
        if (condition.getPreEndTime() != null) {
            sql += " and pt.preEndTime <= '" + condition.getPreEndTime() + "'";
        }
        if (condition.getActStartTime() != null) {
            sql += " and pt.actStartTime >= '" + condition.getActStartTime() + "'";
        }
        if (condition.getActEndTime() != null) {
            sql += " and pt.actEndTime <= '" + condition.getActEndTime() + "'";
        }
        if (condition.getSpotStatus() != null && condition.getSpotStatus() == 0) {
            sql += " and pt.normalNums = pt.spotIds ";
        }
        if (condition.getSpotStatus() != null && condition.getSpotStatus() == 1) {
            sql += " and pt.exceptionNums > 0 ";
        }
        if (condition.getSpotStatus() != null && condition.getSpotStatus() == 2) {
            sql += " and pt.missNums > 0 ";
        }
        if (condition.getSpotStatus() != null && condition.getSpotStatus() == 3) {
            sql += " and pt.supplementNums > 0 ";
        }
        sql += " order by pt.createTime DESC ";
        int size = 0;
        Query query = entityManager.createQuery(sql);
        if (!CollectionUtils.isEmpty(query.getResultList())) {
            size = query.getResultList().size();
        }
        Pagination<Patrol> page = new Pagination<>();
        page.setTotalCount(size);
        if (condition.getPi() != null && condition.getPs() != null) {
            query.setFirstResult(condition.getPi() <= 0 ? 0 :
                    (condition.getPi() - 1) * condition.getPs());
            query.setMaxResults(condition.getPs());
        }
        page.setDataList(query.getResultList());
        return page;
    }
}
