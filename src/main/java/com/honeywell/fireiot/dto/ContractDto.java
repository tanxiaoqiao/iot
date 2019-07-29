package com.honeywell.fireiot.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.constant.ContractStatusEnum;
import com.honeywell.fireiot.constant.CurrencyEnum;
import com.honeywell.fireiot.constant.PayTypeEunm;
import com.honeywell.fireiot.constant.PaymentModeEnum;
import com.honeywell.fireiot.entity.FormData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Date;


/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:12 PM 1/17/2019
 */
@Data
@ApiModel(value = "contract请求对象", description = "contract 请求对象描述")
public class ContractDto {

    @ApiModelProperty( value = "id")
    private  Long id;
    @ApiModelProperty(value = "合同编号")
    private  String contractNo; //合同编号

    @ApiModelProperty( value = "类别id")
    private  Long contractTypeId;//类别id

    @ApiModelProperty(value = "合同名字")
    private String  name;//合同名字

    @ApiModelProperty(value = "金额")
    private double money;//金额

    @ApiModelProperty(value = "货币类型")
    @Enumerated(EnumType.ORDINAL)
    private CurrencyEnum currency;//货币类型

    @ApiModelProperty( value = "货币类型")
    @Enumerated(EnumType.ORDINAL)
    private PaymentModeEnum paymentMode;//支付方式

    @ApiModelProperty( value = "业务员")
    private  String  salemanId;//业务员

    @ApiModelProperty( value = "所属部门")
    private  String departmantId;//所属部门

    @ApiModelProperty( value = "签名日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date signDate;//签名日期

    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startDate;//开始日期

    @ApiModelProperty( value = "截至日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date deadLine;//截至日期


    @ApiModelProperty( value = "支付类型")
    @Enumerated(EnumType.ORDINAL)
    private PayTypeEunm payType;//支付类型

    @ApiModelProperty( value = "合同甲方")
    private  String   partyA;//合同甲方

    @ApiModelProperty(value = "合同乙方")
    private  String    partyB;//合同乙方

    @ApiModelProperty( value = "甲方负责人")
    private  String   partyAPrincipal;//甲方负责人

    @ApiModelProperty( value = "乙方负责人")
    private String    partyBPrincipal;//乙方负责人

    @ApiModelProperty(value = "甲方联系人")
    private String   paryAContact;//甲方联系人

    @ApiModelProperty( value = "乙方联系人")
    private String  paryBContact;//乙方联系人

    @ApiModelProperty( value = "附件名列表")
    private ArrayList<String> fileList;//附件名列表


    @ApiModelProperty( value = "状态")
    @Enumerated(EnumType.ORDINAL)
    private ContractStatusEnum status;

    @ApiModelProperty(value = "表单")
    private FormData formData;
}