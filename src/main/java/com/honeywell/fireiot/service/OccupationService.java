package com.honeywell.fireiot.service;


import com.honeywell.fireiot.entity.Occupation;
import com.honeywell.fireiot.utils.Pagination;

import java.util.List;

/**
 * @project: fire-user
 * @name: OccupationService
 * @author: dexter
 * @create: 2018-12-18 15:30
 * @description:
 **/
public interface OccupationService {

    /**
     * save occupation
     *
     * @param occupation
     */
    void save(Occupation occupation);

    /**
     * soft delete occupation by id
     *
     * @param occupationId
     */
    void delete(String occupationId);

    /**
     * query occupation by id
     *
     * @param id
     * @return
     */
    Occupation findById(String id);

    /**
     * get occupation list
     *
     * @return
     */
    List<Occupation> findAll();

    /**
     * pagination
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Pagination<Occupation> findAllPage(Integer pageIndex, Integer pageSize);
}
