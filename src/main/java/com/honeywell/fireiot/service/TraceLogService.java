package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.TraceLogDto;
import com.honeywell.fireiot.entity.TraceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/*
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:56 AM
 */
public interface TraceLogService {

    TraceLogDto toDto(TraceLog entity);
    List<TraceLogDto> toDto(List<TraceLog> entity);

    Page<TraceLog> findPage();
    Page<TraceLog> findPage(Specification<TraceLog> specification);
    List<TraceLog> find(Specification<TraceLog> specification);

}
