package com.honeywell.fireiot.service;


import com.honeywell.fireiot.entity.InspectionResult;

/**
 * @InterfaceName InspectionService
 * @Description 巡检结果表
 * @Author Monica Z
 * @Date 2019/1/21 10:05
 */
public interface InspectionService {
    long save(InspectionResult inspectionResult);
    void insert(InspectionResult inspectionResult);

}
