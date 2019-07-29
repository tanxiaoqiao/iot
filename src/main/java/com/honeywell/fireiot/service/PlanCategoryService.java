package com.honeywell.fireiot.service;

import com.honeywell.fireiot.entity.PlanCategory;

import java.util.List;

/**
 * @Author: Kayla,Ye
 * @Description:
 * @Date:Created in 4:28 PM 7/19/2018
 */
public interface PlanCategoryService {

    void add(PlanCategory planCategory);

    boolean checkUniqueCategory(String category);

    boolean deleteCategoryById(String id);

    PlanCategory findCategoryById(String id);

    List <PlanCategory> findAll();
}
