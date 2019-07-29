package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.PlanFileSearch;
import com.honeywell.fireiot.entity.PlanFile;
import com.honeywell.fireiot.utils.Pagination;

import java.util.List;

/**
 * @Author: Kayla,Ye
 * @Description:
 * @Date:Created in 4:29 PM 7/19/2018
 */
public interface PlanFileService {

    void add(PlanFile planFile);

    boolean checkUniqueFile(String categoryid,String fileTitle);

    boolean deleteFileById(String id);

    PlanFile findFileById(String id);

    List <PlanFile> findAll();

    List <PlanFile> findByCategryId(String categoryId);

    Pagination <PlanFile> getFile(PlanFileSearch planFileSearch);
}
