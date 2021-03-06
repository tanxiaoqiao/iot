package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.HistoryOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Repository
public interface HistoryOperationRepository extends JpaRepository<HistoryOperation, Long> {


}
