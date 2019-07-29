package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.PlanFileSearch;
import com.honeywell.fireiot.entity.PlanFile;
import com.honeywell.fireiot.repository.PlanFileRespository;
import com.honeywell.fireiot.service.PlanFileService;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Kayla,Ye
 * @Description:
 * @Date:Created in 4:51 PM 7/19/2018
 */

@Service
public class PlanFileServiceImpl implements PlanFileService {
    @Autowired
    PlanFileRespository planFileRespository;

    @Override
    public void add(PlanFile planFile) {

        planFileRespository.save (planFile);
    }

    @Override
    public boolean checkUniqueFile(String categoryid,String fileTitle) {

        if (planFileRespository.findByfileTitle (categoryid,fileTitle) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteFileById(String id) {

        return planFileRespository.removeFileById (id);
    }

    @Override
    public PlanFile findFileById(String id) {
        return planFileRespository.findFileById (id);
    }

    @Override
    public List <PlanFile> findAll() {

        return planFileRespository.findAll ();
    }

    @Override
    public List <PlanFile> findByCategryId(String categoryId) {

        return planFileRespository.findByCategoryId (categoryId);
    }

    @Override
    public Pagination <PlanFile> getFile(PlanFileSearch planFileSearch) {
        return planFileRespository.getFiles (planFileSearch);
    }
}
