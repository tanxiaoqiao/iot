package com.honeywell.fireiot.service;



import com.honeywell.fireiot.dto.DeviceWorkorderCount;
import com.honeywell.fireiot.dto.WorkorderCondition;
import com.honeywell.fireiot.dto.WorkorderDto;
import com.honeywell.fireiot.dto.WorkorderReport;
import com.honeywell.fireiot.entity.LocationWorkOrder;
import com.honeywell.fireiot.entity.Step;
import com.honeywell.fireiot.entity.Workorder;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.vo.SaveWorkorderVo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
public interface WorkorderService {

    /**
     * 流程发布
     *
     * @return
     */
    void deploy();

    /**
     * 添加工单
     *
     * @param saveWorkorderVo
     * @return
     */
    Long addWorkorder(SaveWorkorderVo saveWorkorderVo, Long userId) throws IOException;


    /**
     * 审批工单
     *
     * @return
     */
    Boolean audit(Long workorderId, String accept, Long userId, String reason);

    /**
     * 申请审批
     *
     * @param workorderId
     * @param reason
     * @return
     */
    Boolean applyAudit(Long workorderId, String reason, Long userId, String auditor);

    /**
     * 派单
     *
     * @param workorderId
     * @return
     */
    Boolean arrange(Long workorderId, List<String> acceptors, Long userId);

    /**
     * 接单
     *
     * @param workorderId
     * @return
     */
    Boolean accept(Long workorderId, Long userId);


    /**
     * 拒单
     *
     * @param workorderId
     * @return
     */
    Boolean refuse(Long workorderId, Long userId, String reason);

    /**
     * 工单存档
     *
     * @return
     */
    Boolean trace(Long workorderId, String reason, Long userId) throws Exception;


    /**
     * 完成工单
     *
     * @param workorderId
     * @return
     */
    Boolean complete(Long workorderId, Long userId, List<Step> steps, String attachUrl);


    /**
     * 终止
     *
     * @param
     * @return
     */
    Boolean terminate(Long workorderId, String termiate, Long userId);

    /**
     * 更新
     *
     * @param
     * @return
     */
    Boolean updateWorkorder(Workorder workorder, Long userId);

    /**
     * 工单详情
     *
     * @param workorderId
     * @return
     */
    WorkorderDto findWorkorderById(Long workorderId);


    /**
     * 查询待审核工单
     *
     * @param
     * @return
     *  */
    Page<Workorder> findAuditWorkorder(Specification<Workorder> specification);


    /**
     * 查询工单
     *
     * @param
     * @return
     */
    Pagination<LocationWorkOrder> findWorkorderByTask(WorkorderCondition workorderCondition);

    Page<Workorder> findAll(Specification<Workorder> specification);

    void delete(Long id);

    DeviceWorkorderCount getCount(Long id);

    WorkorderReport getReport(String year);

    Boolean arrangeMul(List<Long> ids, List<String> acceptors,  Long userId ,String workorderTeam);
}
