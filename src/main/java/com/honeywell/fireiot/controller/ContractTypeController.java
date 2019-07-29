package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.ContractTypeDto;
import com.honeywell.fireiot.entity.ContractType;
import com.honeywell.fireiot.service.ContractTypeService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:55 PM 1/18/2019
 */
@RestController
@RequestMapping("/api/contractType")
@Api(tags = {"合同类别管理"})
public class ContractTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractTypeController.class);
    @Autowired
    ContractTypeService contractTypeService;

    @PostMapping
    @ApiOperation(value = "增加合同类别") //方法描述
    public ResponseObject addContractType(@RequestBody ContractTypeDto contractType) {
        if (null == contractType) {
            ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }

        LOGGER.info("addContractType|contractType:{}", contractType);

        if(contractTypeService.findByName(contractType.getName()) != null){
            return ResponseObject.fail(ErrorEnum.CONTRACTTYPE_EXIST);
        }

        contractTypeService.save(contractType);
        return ResponseObject.success(contractType.getId());
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除合同类别") //方法描述
    public ResponseObject deleteContractType(@PathVariable("id") Long id) {
        LOGGER.info("deleteContractType|id:{} ", id);
        //id 是否存在
        if (null == contractTypeService.findById(id)) {
            return ResponseObject.fail(ErrorEnum.CONTRACTTYPE_NOT_EXIST);
        }
        contractTypeService.deleteById(id);
        return ResponseObject.success(null);
    }

    @PutMapping
    @ApiOperation(value = "更新合同类别信息", notes = "更新信息id号为必传") //方法描述
    public ResponseObject updateContractType(@RequestBody ContractTypeDto contractType) {
        if (null == contractType) {
            return ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }
        LOGGER.info("updateContractType|contractType:{} ", contractType);
        ContractType oldContractType = contractTypeService.findById(contractType.getId());
        if (null == oldContractType) {
            return ResponseObject.fail(ErrorEnum.CONTRACTTYPE_NOT_EXIST);
        }

        //名字是否合法
        String oldName = oldContractType.getName ();
        if(contractTypeService.checkUpdateContractType (contractType.getName (),oldName))
        {
            return  ResponseObject.fail (ErrorEnum.CONTRACTTYPE_EXIST);
        }

        contractTypeService.update(contractType);
        return ResponseObject.success(null);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "通过Id查找合同类别") //方法描述
    public ResponseObject findContractType(@PathVariable("id") Long id) {
        LOGGER.info("findContractType|id:{} ", id);
        //id号是否存在
        ContractType contractType = contractTypeService.findById(id);
        if (null == contractType) {
            return ResponseObject.fail(ErrorEnum.CONTRACTTYPE_NOT_EXIST);
        }
        return ResponseObject.success(contractTypeService.toDto(contractType));
    }


    @GetMapping
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<ContractTypeDto>> findPage() {
        Pagination<ContractTypeDto> pagination  = contractTypeService.findPage(JpaUtils.getSpecification());
        return ResponseObject.success(pagination);
    }
}