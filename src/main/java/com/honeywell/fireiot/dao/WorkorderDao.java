package com.honeywell.fireiot.dao;


import com.honeywell.fireiot.dto.WorkorderCondition;
import com.honeywell.fireiot.entity.LocationWorkOrder;
import com.honeywell.fireiot.utils.Pagination;

/**
 * @author: create by kris
 * @description:
 * @date:3/20/2019
 */
public interface WorkorderDao {

  Pagination<LocationWorkOrder> findByCondition(WorkorderCondition workorderCondition);

}
