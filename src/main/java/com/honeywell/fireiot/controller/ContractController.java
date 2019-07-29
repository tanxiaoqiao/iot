package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ContractTitle;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.ContractDto;
import com.honeywell.fireiot.dto.ContractInfoDto;
import com.honeywell.fireiot.entity.Contract;
import com.honeywell.fireiot.service.ContractService;
import com.honeywell.fireiot.service.ContractTypeService;
import com.honeywell.fireiot.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:55 PM 1/18/2019
 */
@RestController
@RequestMapping("/api/contract")
@Api(tags = {"合同管理"})
public class ContractController {
    private static final Logger LOGGER = LoggerFactory.getLogger (ContractController.class);
    @Autowired
    ContractService contractService;

    @Autowired
    ContractTypeService contractTypeService;

    @PostMapping
    @ApiOperation(value = "增加合同") //方法描述
    public ResponseObject addContract (@RequestBody ContractDto contract) {
        if(null == contract ){
            ResponseObject.fail (ErrorEnum.PARAMS_ERROR);
        }
        LOGGER.info ("addContract|contract:{}",contract);

        if(null == contractTypeService.findById(contract.getContractTypeId())){
            return ResponseObject.fail(ErrorEnum.CONTRACTTYPE_NOT_EXIST);
        }

        contractService.save (contract);
        return ResponseObject.success (contract.getId ());
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除合同") //方法描述
    public ResponseObject deleteContract(@PathVariable("id") Long id) {
        LOGGER.info ("deleteContract|id:{} ",id);
        //id 是否存在
        if(null == contractService.findById(id)){
            return ResponseObject.fail (ErrorEnum.CONTRACT_NOT_EXIST);
        }
        contractService.deleteById(id);
        return ResponseObject.success (null);
    }

    @PutMapping
    @ApiOperation(value = "更新合同信息", notes = "更新信息id号为必传") //方法描述
    public ResponseObject updateContract( @RequestBody  ContractDto contract) {
        if(null == contract){
            return  ResponseObject.fail (ErrorEnum.PARAMS_ERROR);
        }
        LOGGER.info ("updateContract|contract:{} ",contract);
        Contract oldContract = contractService.findById (contract.getId ());
        if(null == oldContract ){
            return  ResponseObject.fail (ErrorEnum.CONTRACT_NOT_EXIST);
        }

        contractService.update (contract);
        return ResponseObject.success (null);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "通过Id查找合同") //方法描述
    public ResponseObject findContract(@PathVariable("id") Long id) {
        LOGGER.info ("findContract|id:{} ",id);
        //id号是否存在
        Contract contract = contractService.findById(id);
        if(null == contract){
            return ResponseObject.fail (ErrorEnum.CONTRACT_NOT_EXIST);
        }
        return ResponseObject.success (contract);
    }


    @GetMapping("/contractInfo")
    @ApiOperation(value = "获取所有用于展示的台帐信息")
    public ResponseObject  getAllcontractInfo(){
        List<ContractInfoDto> contractInfoDtoList = contractService.findAllContractInfoDto();
        return ResponseObject.success(contractInfoDtoList);
    }


    @PostMapping("/exportfile")
    @ApiOperation(value = "导出合同文件")
    public ResponseObject createExcel(HttpServletResponse response) throws Exception {
        String  fileName = UUID.randomUUID()+".xlsx";

        //构造excelData
        ExcelData data = new ExcelData();
        data.setFileName(fileName);
        data.setTitle(ContractTitle.CONTRACT_TITLELIST);

        List<ContractInfoDto> contractInfoDtoList = contractService.findAllContractInfoDto();
        if(null == contractInfoDtoList){
            return ResponseObject.fail(ErrorEnum.CONTRACT_NOT_EXIST);
        }

        //构造表格数据
        List<List<Object>> rowData = new ArrayList<>();
        for(ContractInfoDto contractInfoDto: contractInfoDtoList){
            List<Object> row = new ArrayList<>();
            row.add(contractInfoDto.getContractNo());
            row.add(contractInfoDto.getName());
            row.add(contractInfoDto.getContractTypeName());
            row.add(contractInfoDto.getContactMoney());
            row.add(contractInfoDto.getSaleman());
            row.add(contractInfoDto.getPayType());
            row.add(contractInfoDto.getSignDate());
            row.add(contractInfoDto.getStartDate());
            row.add(contractInfoDto.getDeadLine());
            row.add(contractInfoDto.getStatus());
            rowData.add(row);
        }
        data.setRows(rowData);

        ExcelUtil.exportExcel(response,data);
        return ResponseObject.success(null);
    }

    @GetMapping
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject  findPage() {
        Pagination<ContractInfoDto> pagination  = contractService.findPage(JpaUtils.getSpecification());
        return ResponseObject.success(pagination);
    }

}
