package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.ContractTypeDto;
import com.honeywell.fireiot.entity.ContractType;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 9:43 AM 1/18/2019
 */
public interface ContractTypeService {

    void save(ContractTypeDto contractTypeDto);

    void delete(ContractType contractType);

    void deleteById(Long id);

    ContractType findById(Long id);

    ContractType findByName(String name);

    void update(ContractTypeDto contractDto);

    List<ContractType> findAll();

    ContractType toEntity(ContractTypeDto contractTypeDto);

    ContractTypeDto toDto(ContractType contractType);


    boolean checkUpdateContractType(String name, String oldName);

    List<ContractType> findByStatus(Boolean status);

    Pagination<ContractTypeDto> findPage(Specification<ContractType> specification);
}
