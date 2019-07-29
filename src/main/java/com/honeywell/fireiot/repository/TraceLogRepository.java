package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.TraceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TraceLogRepository extends JpaRepository<TraceLog, Long>, JpaSpecificationExecutor<TraceLog> {
}
