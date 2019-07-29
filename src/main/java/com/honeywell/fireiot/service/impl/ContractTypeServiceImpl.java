package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.constant.ReminderTypeEnum;
import com.honeywell.fireiot.dto.ContractTypeDto;
import com.honeywell.fireiot.entity.ContractType;
import com.honeywell.fireiot.entity.Employee;
import com.honeywell.fireiot.entity.FormStructure;
import com.honeywell.fireiot.entity.WorkTeam;
import com.honeywell.fireiot.repository.ContractTypeRepository;
import com.honeywell.fireiot.repository.EmployeeRepository;
import com.honeywell.fireiot.repository.FormStructureRepository;
import com.honeywell.fireiot.repository.WorkTeamRepository;
import com.honeywell.fireiot.service.ContractTypeService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 9:54 AM 1/18/2019
 */
@Service
public class ContractTypeServiceImpl implements ContractTypeService {

    @Autowired
    ContractTypeRepository contractTypeRepository;

    @Autowired
    FormStructureRepository formStructureRepository;

    @Autowired
    WorkTeamRepository workTeamRepository;

    @Autowired
    EmployeeRepository employeeRepository;



    @Override
    @Transactional(rollbackOn = Exception.class)
    public void save(ContractTypeDto contractTypeDto) {

        ContractType contractType = toEntity(contractTypeDto);

        //生成新的表单实体
        FormStructure formStructure = new FormStructure();
        formStructure.setName(contractType.getName());
        formStructure.setDescription(contractType.getDescription());
        Long formId  = formStructureRepository.save(formStructure).getId();
        contractType.setFormStructureId(formId);

        contractTypeDto.setId( contractTypeRepository.saveAndFlush(contractType).getId());

    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(ContractType contractType) {
        contractTypeRepository.delete(contractType) ;
        formStructureRepository.deleteById(contractType.getFormStructureId());
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteById(Long id) {

        ContractType contractType = contractTypeRepository.findById(id).orElse(null);
        if(contractType != null){
            formStructureRepository.deleteById(contractType.getFormStructureId());
        }
        contractTypeRepository.deleteById(id);


    }

    @Override
    public ContractType findById(Long id) {
        Optional<ContractType> contractTypeOptional = contractTypeRepository.findById(id);
        if(contractTypeOptional.isPresent()){
            return  contractTypeOptional.get();
        }

        return  null;
    }

    @Override
    public ContractType findByName(String name) {
        List<ContractType> contractTypeList =  contractTypeRepository.findByName(name);
        if(contractTypeList.isEmpty()){
            return  null;
        }else{
            return  contractTypeList.get(0);
        }
    }

    @Override
    public void update(ContractTypeDto contractTypeDto) {
        ContractType  contractType= contractTypeRepository.getOne(contractTypeDto.getId());
        if(contractType != null){
            BeanUtils.copyProperties(contractTypeDto, contractType);
            contractTypeRepository.save(contractType);
        }
    }

    @Override
    public List<ContractType> findAll() {
        return contractTypeRepository.findAll();
    }

    @Override
    public ContractType toEntity(ContractTypeDto contractTypeDto) {
        ContractType contractType = new ContractType();
        BeanUtils.copyProperties(contractTypeDto,contractType);
        return  contractType;
    }

    @Override
    public ContractTypeDto toDto(ContractType contractType) {
        ContractTypeDto contractTypeDto = new ContractTypeDto();
        BeanUtils.copyProperties(contractType, contractTypeDto);

        List<String> reminders = contractType.getReminders();
        if( (null == reminders) || (reminders.isEmpty()) ){
            return contractTypeDto;
        }

        ReminderTypeEnum type = contractType.getReminderType();
        List<String> reminderName = new ArrayList<>();
        if(ReminderTypeEnum.GROUP == type){
            for(String id: reminders){
                WorkTeam workTeam = workTeamRepository.findById(id).orElse(null);
                if(workTeam != null){
                    reminderName.add(workTeam.getTeamName());
                }
            }

        }else{
            for(String id:reminders){
                Employee employee = employeeRepository.findById(id).orElse(null);
                if(employee != null){
                    reminderName.add(employee.getName());
                }
            }

        }

        contractTypeDto.setReminderName((ArrayList<String>) reminderName);
        return  contractTypeDto;
    }

    @Override
    public boolean checkUpdateContractType(String name, String oldName) {
        List<ContractType> contractTypeList = contractTypeRepository.findByName(name);

        if(contractTypeList.size() > 1){
            return true;
        }
        if( ( 1 == contractTypeList.size()) && (!(contractTypeList.get(0).getName().equals(oldName)))){
            return true;
        }

        return false;
    }

    @Override
    public List<ContractType> findByStatus(Boolean status) {
        return contractTypeRepository.findByIsRemind(status);
    }

    @Override
    public Pagination<ContractTypeDto > findPage(Specification<ContractType> specification) {
        Page<ContractType> page = contractTypeRepository.findAll(specification, JpaUtils.getPageRequest());
        int count = (int)page.getTotalElements();
        List<ContractType> contractTypeList =  page.getContent();

        if(count <= 0 ){
            return  new Pagination<>(count, null);
        }
        List<ContractTypeDto> contractTypeDtos = new ArrayList<>();
        for(ContractType contractType: contractTypeList){
            ContractTypeDto contractTypeDto = toDto(contractType);
            contractTypeDtos.add(contractTypeDto);
        }

        return new Pagination<ContractTypeDto>(count,  contractTypeDtos);
    }
}
