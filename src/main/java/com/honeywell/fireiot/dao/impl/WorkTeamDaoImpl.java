package com.honeywell.fireiot.dao.impl;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dao.WorkTeamDao;
import com.honeywell.fireiot.entity.WorkTeam;
import com.honeywell.fireiot.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Repository
@Transactional(rollbackOn = Exception.class)
public class WorkTeamDaoImpl implements WorkTeamDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public WorkTeam update(WorkTeam workTeam) {
        WorkTeam team = entityManager.find(WorkTeam.class, workTeam.getId());
        if (team == null) {
            throw new BusinessException(ErrorEnum.TEAM_NULL);
        }
        if (StringUtils.isNotEmpty(workTeam.getTeamName())) {
            team.setTeamName(workTeam.getTeamName());
        }
        if (workTeam.getWorkerIds() != null ) {
            team.setWorkerIds(workTeam.getWorkerIds());
        }
        if (workTeam.getTracerIds() != null ) {
            team.setTracerIds(workTeam.getTracerIds());
        }
        if (workTeam.getSaveIds() != null ) {
            team.setSaveIds(workTeam.getSaveIds());
        }
        if (workTeam.getAuditIds() != null ) {
            team.setAuditIds(workTeam.getAuditIds());
        }
        if (workTeam.getVerifyIds() != null ) {
            team.setVerifyIds(workTeam.getVerifyIds());
        }
        String[] auditIds = workTeam.getAuditIds();
        String[] workerIds = workTeam.getWorkerIds();
        String[] verifyIds = workTeam.getVerifyIds();
        String[] tracerIds = workTeam.getTracerIds();
        String[] saveIds = workTeam.getSaveIds();
        List<String> audits = Arrays.asList(auditIds);
        Set<String> set = new HashSet<String>(8);
        set.addAll(audits);
        set.addAll(Arrays.asList(workerIds));
        set.addAll(Arrays.asList(verifyIds));
        set.addAll(Arrays.asList(tracerIds));
        set.addAll(Arrays.asList(saveIds));
        String[] all = set.toArray(new String[set.size()]);
        team.setAllIds(all);
       return entityManager.merge(team);

    }

}
