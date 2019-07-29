package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 4:36 PM 1/17/2019
 */
@Repository
public interface ContractTypeRepository extends JpaRepository<ContractType, Long > , JpaSpecificationExecutor<ContractType> {
    List<ContractType> findByName(String name);

    List<ContractType> findByIsRemind(Boolean isRemind);
}
