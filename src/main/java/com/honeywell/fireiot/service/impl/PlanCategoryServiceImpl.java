package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.entity.PlanCategory;
import com.honeywell.fireiot.repository.PlanCategoryRespository;
import com.honeywell.fireiot.repository.PlanFileRespository;
import com.honeywell.fireiot.service.PlanCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Kayla,Ye
 * @Description:
 * @Date:Created in 4:35 PM 7/19/2018
 */
@Service
public class PlanCategoryServiceImpl implements PlanCategoryService {

    @Autowired
    PlanCategoryRespository planCategoryRespository;

    @Autowired
    PlanFileRespository planFileRespository;

    @Override
    public void add(PlanCategory planCategory) {

        planCategoryRespository.save (planCategory);

    }

    @Override
    public boolean checkUniqueCategory(String category) {
        if (planCategoryRespository.findPlanByCategory (category) != null) {

            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCategoryById(String id) {
        planFileRespository.removeFileByCategoryId (id);
        return planCategoryRespository.removeAreaById (id);
    }

    @Override
    public PlanCategory findCategoryById(String id) {
        return planCategoryRespository.findPlanByCategoryId (id);
    }

    @Override
    public List <PlanCategory> findAll() {
        return planCategoryRespository.findAll ();
    }
}
