package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.MobPatrolVo;

import java.util.List;


/**
 * @author: create by kris
 * @description:
 * @date:1/25/2019
 */
public interface EmpPatrolService {

    List<MobPatrolVo> findByEmpId(String empId);
}
