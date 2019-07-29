package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.MaintenanceDto;
import com.honeywell.fireiot.entity.DailyMaintenance;
import com.honeywell.fireiot.entity.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
public interface MaintenanceService {


    void addMaintenance(Maintenance maintenance);


    void deleteMaintenance(Long id);


    void updateMaintenance(Maintenance maintenance);


    Page<Maintenance> findByCondition(Specification<Maintenance> specification);


    Page<DailyMaintenance> getDailyByCondition(Specification<DailyMaintenance> specification);

    MaintenanceDto findByid(Long id);

}
