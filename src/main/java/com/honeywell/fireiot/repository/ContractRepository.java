package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 4:33 PM 1/17/2019
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract,Long>, JpaSpecificationExecutor<Contract> {
    List<Contract> findByContractTypeId(Long contractTypeId);

    List<Contract> findByContractTypeIdAndAndDeadLine(Long contractTypeId, Date deadLine);

}
