package com.honeywell.fireiot.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.constant.ContractStatusEnum;
import com.honeywell.fireiot.constant.PayTypeEunm;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;


/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:13 PM 1/17/2019
 */
@Data
public class ContractInfoDto {

    private  Long id;//合同Id

    private  String contractNo; //合同编号

    private String  name;//合同名字

    private String contractTypeName;//合同类型

    private String contactMoney;//金额

    @Enumerated(EnumType.ORDINAL)
    private PayTypeEunm payType;//支付类型

    private  String  saleman;//业务员

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date signDate;//签名日期

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startDate;//开始日期

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date  deadLine;//截至日期

    @Enumerated(EnumType.STRING)
    private ContractStatusEnum status;

    private String formDataId;//表单id

    private  String  salemanId;//业务员
}
