package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.dto.MobPatrolVo;
import com.honeywell.fireiot.entity.EmpPatrol;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.repository.EmpPatrolRepository;
import com.honeywell.fireiot.service.EmpPatrolService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/25/2019
 */
@Service
public class EmpPatrolServiceImpl implements EmpPatrolService {

    @Autowired
    EmpPatrolRepository empPatrolRepository;

    @Override
    public List<MobPatrolVo> findByEmpId(String empId) {
        List<EmpPatrol> ep = empPatrolRepository.findByEmployeeId(empId);
        if (CollectionUtils.isEmpty(ep)) {
            return null;
        }
        List<MobPatrolVo> pt = new ArrayList<>();
        //只查看未完成的巡检
        ep.forEach(e -> {
            List<Patrol> patrols = e.getPatrols();
            if (patrols != null) {
                patrols.forEach(p -> {
                    if (p.getStatus() == 0 ) {
                        MobPatrolVo mp = new MobPatrolVo();
                        BeanUtils.copyProperties(p, mp);
                        pt.add(mp);
                    }
                });
            }
        });
        return pt;
    }
}
