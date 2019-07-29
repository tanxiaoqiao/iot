package com.honeywell.fireiot.service.impl;



import com.honeywell.fireiot.constant.CurrencySign;
import com.honeywell.fireiot.constant.ReminderTypeEnum;
import com.honeywell.fireiot.dto.ContractDto;
import com.honeywell.fireiot.dto.ContractInfoDto;
import com.honeywell.fireiot.entity.*;
import com.honeywell.fireiot.repository.*;
import com.honeywell.fireiot.service.ContractService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.honeywell.fireiot.constant.CurrencyEnum.DOLLAR;
import static com.honeywell.fireiot.constant.CurrencyEnum.RMB;


/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 9:46 AM 1/18/2019
 */
@Transactional(rollbackOn = Exception.class)
@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    ContractTypeRepository contractTypeRepository;

    @Autowired
    WorkTeamRepository workTeamRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    FormDataRepository formDataRepository;

    @Override
    public void save(ContractDto contractDto) {

        Contract contract = toEntity(contractDto);
        contractDto.setId( contractRepository.saveAndFlush(contract).getId());
    }

    @Override
    public void delete(Contract contract) {
        contractRepository.delete(contract);
        if(!StringUtils.isEmpty(contract.getFormDataId())){
            formDataRepository.delete(contract.getFormDataId());
        }
    }

    @Override
    public void deleteById(Long id) {
        Contract contract = contractRepository.findById(id).orElse(null);
        contractRepository.deleteById(id);
        if( (contract != null) && (!StringUtils.isEmpty(contract.getFormDataId())) ){
            formDataRepository.delete(contract.getFormDataId());
        }

    }

    @Override
    public Contract findById(Long id) {
        Optional<Contract> contract = contractRepository.findById(id);
        if(contract.isPresent()){
            return contract.get();
        }
        return null;
    }

    @Override
    public void update(ContractDto contractDto) {
        Contract contract = contractRepository.getOne(contractDto.getId());
        if (contract != null) {
            BeanUtils.copyProperties(contractDto, contract);
            contractRepository.save(contract);
        }
        FormData formDataDto = contractDto.getFormData();
        if(null == formDataDto){
            return;
        }
        FormData formData = formDataRepository.findOne(formDataDto.getId());
        if(null == formData){
            return;
        }
        BeanUtils.copyProperties(formDataDto, formData);
        formDataRepository.save(formData);
    }
    @Override
    public Contract toEntity(ContractDto contractDto) {
        Contract contract = new Contract();
        BeanUtils.copyProperties(contractDto,contract);
        if(contractDto.getFormData() != null){
            contract.setFormDataId(formDataRepository.insert(contractDto.getFormData()).getId());
        }
        return contract;
    }

    @Override
    public ContractInfoDto toContractInfoDto(Contract contract) {
        ContractInfoDto contractInfoDto = new ContractInfoDto();
        BeanUtils.copyProperties(contract, contractInfoDto);

        if(contract.getContractTypeId() != null) {
            Optional<ContractType> contractType = contractTypeRepository.findById(contract.getContractTypeId());
            if (contractType.isPresent()) {
                contractInfoDto.setContractTypeName(contractType.get().getName());
            }

        }

        String money = "";


        Enum  currencyEnum = contract.getCurrency();
        if(currencyEnum != null){
            if( currencyEnum.equals(RMB)){
                money += CurrencySign.RMB_SIGN;
            }else if(currencyEnum.equals(DOLLAR)) {
                money += CurrencySign.DOLLAR_SIGN;
            }
        }

        money += contract.getMoney();
        contractInfoDto.setContactMoney(money);
        //根据salemanid获取saleMan值

        String    saleManId =  contract.getSalemanId();
        if(saleManId != null){
            Employee employee = employeeRepository.findById(saleManId).orElse(null);
            if(employee != null){
                contractInfoDto.setSaleman(employee.getName());
            }
        }
        return  contractInfoDto;
    }

    @Override
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    @Override
    public List<ContractInfoDto> findAllContractInfoDto() {
        List<Contract> contractList = contractRepository.findAll();
        return  getContractInfoDtoList(contractList);
    }

    @Override
    public List<Contract> findByContractType(Long id) {
        return contractRepository.findByContractTypeId(id);
    }

    @Override
    public List<Contract> findByContractTypeAndDeadLine(Long contractTypeId, Date date) {
        return contractRepository.findByContractTypeIdAndAndDeadLine(contractTypeId, date);
    }

    @Override
    public List<String> findRedmindIds(Contract contract) {
        Long contractTypeId = contract.getContractTypeId();
        if(null == contractTypeId){
            return null;
        }

        ContractType contractType = contractTypeRepository.findById(contractTypeId).orElse(null);
        if( (null == contractType) ||  (false == contractType.getIsRemind()) ){
            return null;
        }

        List<String> redminderId = new ArrayList<>();
        if(contractType.getIsNotify()){
            redminderId.add(contract.getSalemanId());
        }


        ReminderTypeEnum reminderTypeEnum = contractType.getReminderType();
        //工作组
        if(reminderTypeEnum.equals(ReminderTypeEnum.GROUP)){
            List<String> groupIds = contractType.getReminders();
            for(String groupId: groupIds ){
                WorkTeam workTeam = workTeamRepository.findById(groupId).orElse(null);
                if(null == workTeam){
                    continue;
                }

                String[] ids = workTeam.getAllIds();
                if(StringUtils.isEmpty(ids)){
                    continue;
                }
                int size = ids.length;

                for(int i = 0; i < size; i++){
                    redminderId.add(ids[i]);
                }
            }
        }
        else{
            List<String> reminders = contractType.getReminders();
            for(String reminder:reminders){
                redminderId.add(reminder);
            }
        }

        return redminderId;
    }

    @Override
    public Pagination<ContractInfoDto> findPage(Specification<Contract> specification) {
        Page<Contract> page = contractRepository.findAll(specification, JpaUtils.getPageRequest());
        List<Contract> entityList =  page.getContent();
        List<ContractInfoDto> dtoList = getContractInfoDtoList(entityList);
        return new Pagination<ContractInfoDto>((int) page.getTotalElements(), dtoList);
    }

    private List<ContractInfoDto> getContractInfoDtoList(List<Contract> contractList) {
        List<ContractInfoDto> contractInfoDtoList = new ArrayList<>();
        if(!contractList.isEmpty()){
            for(Contract contract: contractList){
                contractInfoDtoList.add(toContractInfoDto(contract));
            }
        }
        else {
            return  null;
        }

        return  contractInfoDtoList;
    }


}

