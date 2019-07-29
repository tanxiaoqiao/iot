package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.entity.InspectionResult;
import com.honeywell.fireiot.repository.InspectionResultRepository;
import com.honeywell.fireiot.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName InspectionServiceImpl
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/21 10:08
 */
@Service
public class InspectionServiceImpl implements InspectionService {

    @Autowired
    private InspectionResultRepository iRRepository;

    @Override
    public long save(InspectionResult inspectionResult) {
        if (inspectionResult.getId() == 0) {
            this.insert(inspectionResult);
        } else {
            inspectionResult.setUpdateTime(new Date());
            iRRepository.save(inspectionResult);
        }
        return inspectionResult.getId();
    }

    @Override
    public void insert(InspectionResult inspectionResult) {
        inspectionResult.setCreateTime(new Date());
        iRRepository.save(inspectionResult);

    }
}
