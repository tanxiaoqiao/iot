package com.honeywell.fireiot.dao;


import com.honeywell.fireiot.dto.PatrolCondition;
import com.honeywell.fireiot.entity.Patrol;
import com.honeywell.fireiot.utils.Pagination;

/**
 * @author: create by kris
 * @description:
 * @date:1/8/2019
 */
public interface PatrolDao {

    Pagination<Patrol> findByCondition(PatrolCondition condition);
}
