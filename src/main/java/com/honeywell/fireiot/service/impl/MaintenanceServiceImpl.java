package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.BusinessDeviceDto;
import com.honeywell.fireiot.dto.MaintenanceDto;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.DailyMaintenanceReporsitory;
import com.honeywell.fireiot.repository.MaintenanceReporsitory;
import com.honeywell.fireiot.repository.WorkorderRepository;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.service.MaintenanceService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Service
public class MaintenanceServiceImpl implements MaintenanceService {


    /**
     * 日历结束时间
     */
    private static final String END_DATE = "2070-01-01 00:00:00";

    @Autowired
    DailyMaintenanceReporsitory dailyMaintenanceReporsitory;

    @Autowired
    MaintenanceReporsitory maintenanceReporsitory;

    @Autowired
    WorkorderRepository workorderRepository;

    @Autowired
    BusinessDeviceService businessDeviceService;

    @Autowired
    LocationService locationService;


    @Transactional(rollbackOn = Exception.class)
    @Override
    public void addMaintenance(Maintenance maintenance) {
        if (maintenance.getEndMonth() < maintenance.getStartMonth()) {
            throw new BusinessException(ErrorEnum.PARAMETER_ERROR);
        }
        Maintenance save = maintenanceReporsitory.save(maintenance);
        Calendar start = Calendar.getInstance();
        start.setTime(maintenance.getStartTime());
        Calendar end = Calendar.getInstance();
        Timestamp timestamp = Timestamp.valueOf(END_DATE);
        end.setTime(timestamp);
        //日 周就换算成日
        Integer startMonth = maintenance.getStartMonth() - 1;
        Integer endMonth = maintenance.getEndMonth() - 1;
        if (maintenance.getType() == 0) {

            while (start.before(end)) {
                start.add(Calendar.DATE, maintenance.getTimes());
                //判断是否属于当月
                int month = start.get(Calendar.MONTH);
                if (month <= endMonth && month >= startMonth) {
                    saveDailyMaintenance(maintenance, start.getTime());
                }
            }
            //周
        } else if (maintenance.getType() == 1) {
            while (start.before(end)) {
                start.add(Calendar.DATE, maintenance.getTimes() * 7);
                int month = start.get(Calendar.MONTH);
                if (month <= endMonth && month >= startMonth) {
                    saveDailyMaintenance(maintenance, start.getTime());
                }
            }

            //月
        } else if (maintenance.getType() == 2) {
            while (start.before(end)) {
                start.add(Calendar.MONTH, maintenance.getTimes());
                int month = start.get(Calendar.MONTH);
                if (month <= endMonth && month >= startMonth) {
                    saveDailyMaintenance(maintenance, start.getTime());
                }
            }
            //年
        } else if (maintenance.getType() == 3) {
            while (start.before(end)) {
                start.add(Calendar.YEAR, maintenance.getTimes());
                int month = start.get(Calendar.MONTH);
                if (month <= endMonth && month >= startMonth) {
                    saveDailyMaintenance(maintenance, start.getTime());
                }
            }
        }


    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public void deleteMaintenance(Long id) {
        maintenanceReporsitory.deleteById(id);
        dailyMaintenanceReporsitory.deleteByMaintenanceId(id, new Date());
    }

    @Override
    public void updateMaintenance(Maintenance maintenance) {
        deleteMaintenance(maintenance.getId());
        addMaintenance(maintenance);
    }

    @Override
    public Page<Maintenance> findByCondition(Specification<Maintenance> specification) {
        return maintenanceReporsitory.findAll(specification, JpaUtils.getPageRequest());

    }


    @Override
    public Page<DailyMaintenance> getDailyByCondition(Specification<DailyMaintenance> specification) {
        Page<DailyMaintenance> all = dailyMaintenanceReporsitory.findAll(specification, JpaUtils.getPageRequest());
        return all;
    }

    @Override
    public MaintenanceDto findByid(Long id) {
        Maintenance maintenance = maintenanceReporsitory.findById(id).orElse(null);
        MaintenanceDto md = MaintenanceDto.toDto(maintenance);
        List<Location> all = locationService.findAll(maintenance.getLocationIds());
        List<WorkorderLocation> los = all.stream().map(m -> {
            return WorkorderLocation.toDto(m);
        }).collect(Collectors.toList());
        List<WorkorderDevice> dlists = maintenance.getDeviceIds().stream().map(k -> {
            BusinessDeviceDto byId = businessDeviceService.findById(k);
            return WorkorderDevice.toDto(byId);
        }).collect(Collectors.toList());
        md.setLocationIds(los);
        md.setDeviceIds(dlists);
        return md;

    }

    private void saveDailyMaintenance(Maintenance maintenance, Date time) {
        DailyMaintenance dm = new DailyMaintenance();
        dm.setMaintenanceId(maintenance.getId());
        dm.setStartTime(new Timestamp(time.getTime()));
        dm.setIsAuto(maintenance.getIsAuto());
        dm.setWorkDays(maintenance.getWorkDays());
        dm.setDays(maintenance.getDays());
        dm.setName(maintenance.getName());
        dailyMaintenanceReporsitory.save(dm);

    }
}
