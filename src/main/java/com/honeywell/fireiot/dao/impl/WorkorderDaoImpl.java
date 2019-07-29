package com.honeywell.fireiot.dao.impl;

import com.honeywell.fireiot.dao.WorkorderDao;
import com.honeywell.fireiot.dto.WorkorderCondition;
import com.honeywell.fireiot.entity.LocationWorkOrder;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Repository
@Transactional(rollbackOn = Exception.class)
public class WorkorderDaoImpl implements WorkorderDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Pagination<LocationWorkOrder> findByCondition(WorkorderCondition workorderEmp) {
        String sql = "select DISTINCT (we.id),  we.creator,we.creatorName, we.title,we.status," +
                "we.level, we.type,we.workTeamId,we.workTeamName,we.auditId,we.auditName," +
                "we.description,we.preStartTime,we.preEndTime, we.actStartTime,we.actEndTime ";
        sql += " From LocationWorkOrder we where 1=1 ";
        if (workorderEmp.getEmployeeId() != null) {
            sql += "and we.employeeId='" + workorderEmp.getEmployeeId() + "'";
        }
        if (workorderEmp.getType() != null) {
            sql += " and we.type= " + workorderEmp.getType();
        }
        if (!CollectionUtils.isEmpty(workorderEmp.getStatus())) {
            sql += "  and ( we.status= " + workorderEmp.getStatus().get(0);
            for (int i = 1; i < workorderEmp.getStatus().size(); i++) {
                sql += " or we.status = " + workorderEmp.getStatus().get(i);
            }
            sql += ")";
        }
        if (workorderEmp.getDeviceId() != null) {
            sql += " and we.deviceId= " + workorderEmp.getDeviceId();
        }
        if (workorderEmp.getLocationId() != null) {
            sql += " and we.locationId in  " + workorderEmp.getLocationIds();
        }
        if (workorderEmp.getId() != null) {
            sql += " and we.Id= " + workorderEmp.getId();
        }
        if (workorderEmp.getLevel() != null) {
            sql += " and we.level= " + workorderEmp.getLevel();
        }
        if (workorderEmp.getCreator() != null) {
            sql += " and we.creator= '" + workorderEmp.getCreator() + "'";
        }

        if (workorderEmp.getCreateTimeStart() != null) {
            sql += " and we.createTime  >='" + workorderEmp.getCreateTimeStart() + "'";
        }
        if (workorderEmp.getCreateTimeEnd() != null) {
            sql += " and we.createTime  <='" + workorderEmp.getCreateTimeEnd() + "'";
        }
        sql += " order by we.id DESC";
        Query query = entityManager.createQuery(sql);
        Pagination<LocationWorkOrder> page = new Pagination<>();
        if (query == null || CollectionUtils.isEmpty(query.getResultList())) {
            return null;
        }
        page.setTotalCount(query.getResultList().size());
        query.setFirstResult((workorderEmp.getPi() - 1) * workorderEmp.getPs());

        query.setMaxResults(workorderEmp.getPs());
        List<Object[]> resultList = query.getResultList();
        List<LocationWorkOrder> collect = resultList.stream().map(l -> {
            LocationWorkOrder lw = new LocationWorkOrder();
            lw.setId((Long) l[0]);
            lw.setCreator(String.valueOf(l[1]));
            lw.setCreatorName(String.valueOf(l[2]));
            lw.setTitle(String.valueOf(l[3]));
            lw.setStatus((Integer.parseInt(String.valueOf(l[4])  != null ? String.valueOf(l[4]) : "0")));
            lw.setLevel((Integer.parseInt(String.valueOf(l[5]) != null ? String.valueOf(l[5]) : "0")));
            lw.setType((Integer.parseInt(String.valueOf(l[6]) != null ? String.valueOf(l[6]) : "0")));
            lw.setWorkTeamId(String.valueOf(l[7]));
            lw.setWorkTeamName(String.valueOf(l[8]));
            lw.setAuditId(String.valueOf(l[9]));
            lw.setAuditName(String.valueOf(l[10]));
            lw.setDescription(String.valueOf(l[11]));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date str = null;//时间存储为字符串
            try {
                if (String.valueOf(l[12]) != null) {
                    str = sdf.parse(String.valueOf(l[12]));
                    lw.setPreStartTime(new Timestamp(str.getTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return lw;
        }).collect(Collectors.toList());
        page.setDataList(collect);
        return page;

    }


}
