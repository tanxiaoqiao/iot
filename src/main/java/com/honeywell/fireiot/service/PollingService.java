package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.PollingDto;
import com.honeywell.fireiot.entity.Polling;
import com.honeywell.fireiot.utils.Pagination;
import org.quartz.SchedulerException;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
public interface PollingService {


    Long addPolling(Polling polling) throws SchedulerException;

    Boolean deletePolling(Long pollingId) throws SchedulerException;

    PollingDto findone(Long pollingId);

    Pagination<PollingDto> findByCondition(Specification<Polling> specification);

    Boolean update(Polling polling) throws SchedulerException;

    /**
     * 激活/失效巡检
     *
     * @param pollingId
     * @param activated
     * @return
     * @throws SchedulerException
     */
    Boolean changeStatus(Long pollingId, Boolean activated) throws SchedulerException;


    void repairAllTrigger() throws SchedulerException;
}
