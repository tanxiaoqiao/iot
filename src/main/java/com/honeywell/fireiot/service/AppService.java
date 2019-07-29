package com.honeywell.fireiot.service;

import com.honeywell.fireiot.vo.AppPatrolVO;
import com.honeywell.fireiot.utils.ListPage;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppService
 * @author: dexter
 * @create: 2019-03-19 13:48
 * @description:
 **/
public interface AppService {

    /**
     * 根据员工id现在所有未完成巡检内容
     *
     * @param employeeId
     * @param pageSize
     * @param pageNum
     * @return
     */
    ListPage<AppPatrolVO> download(String employeeId, Integer pageSize, Integer pageNum);
}
