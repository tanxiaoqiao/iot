package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.ContractDto;
import com.honeywell.fireiot.dto.ContractInfoDto;
import com.honeywell.fireiot.entity.Contract;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 9:41 AM 1/18/2019
 */
public interface ContractService {


  void save(ContractDto contractDto);

  void delete(Contract contract);

  void deleteById(Long id);

  Contract findById(Long id);

  void update(ContractDto contractDto);

  Contract toEntity(ContractDto contractDto);

  ContractInfoDto toContractInfoDto(Contract contract);

  List<Contract> findAll();

  List<ContractInfoDto> findAllContractInfoDto();

  List<Contract> findByContractType(Long id);

  List<Contract> findByContractTypeAndDeadLine(Long contractTypeId, Date date);

  List<String>  findRedmindIds(Contract contract);

  Pagination<ContractInfoDto> findPage(Specification<Contract> specification);
}