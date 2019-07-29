package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.TraceLogDto;
import com.honeywell.fireiot.entity.TraceLog;
import com.honeywell.fireiot.repository.TraceLogRepository;
import com.honeywell.fireiot.service.TraceLogService;
import com.honeywell.fireiot.utils.EnvHolder;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/3/14 10:30 AM
 */
@Service
public class TraceLogServiceImpl implements TraceLogService {

    @Autowired
    TraceLogRepository logRepository;

    @Override
    public TraceLogDto toDto(TraceLog entity) {
        TraceLogDto dto = new TraceLogDto();
        BeanUtils.copyProperties(entity, dto);
        dto.setType(entity.getType().getDescription());
        return dto;
    }

    @Override
    public List<TraceLogDto> toDto(List<TraceLog> entity) {
        List<TraceLogDto> logArrayList = new ArrayList<TraceLogDto>();
        entity.forEach(e -> {
            logArrayList.add(toDto(e));
        });
        return logArrayList;
    }


    @Override
    public Page<TraceLog> findPage() {

        return logRepository.findAll(JpaUtils.getSpecification().and(buildCommonSpecification()), JpaUtils.getPageRequest());
    }

    @Override
    public List<TraceLog> find(Specification<TraceLog> specification){
        return logRepository.findAll(specification.and(buildCommonSpecification()));
    }

    @Override
    public Page<TraceLog> findPage(Specification<TraceLog> specification) {
        return logRepository.findAll(specification.and(buildCommonSpecification()), JpaUtils.getPageRequest());
    }

    private Specification buildCommonSpecification() {
        Specification specification = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get("resource"), EnvHolder.getHolder().getResource());
            query.where(predicate);
            return query.getRestriction();
        };
        return specification;
    }
}
