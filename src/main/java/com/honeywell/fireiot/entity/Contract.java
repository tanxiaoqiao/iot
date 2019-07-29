package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.constant.ContractStatusEnum;
import com.honeywell.fireiot.constant.CurrencyEnum;
import com.honeywell.fireiot.constant.PayTypeEunm;
import com.honeywell.fireiot.constant.PaymentModeEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * @Author: Kayla, Ye
 * @Description: 合同基本信息
 * @Date:Created in 2:08 PM 1/17/2019
 */
@Entity
@Table(name = "con_contract")
@Data
public class Contract extends BaseEntity<Contract> {
    private  String contractNo; //合同编号

    private  Long contractTypeId;//类别id

    private String  name;//合同名字

    private double money;//金额

    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;//金额

    @Enumerated(EnumType.STRING)
    private PaymentModeEnum paymentMode;//支付方式

    private  String  salemanId;//业务员

    private  String departmantId;//所属部门

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date signDate;//签名日期

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startDate;//开始日期

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date deadLine;//截至日期

    @Enumerated(EnumType.STRING)
    private PayTypeEunm payType;//支付类型

    private  String   partyA;//合同甲方

    private  String    partyB;//合同乙方

    private  String   partyAPrincipal;//甲方负责人

    private String    partyBPrincipal;//乙方负责人

    private String   paryAContact;//甲方联系人

    private String  paryBContact;//乙方联系人

    private ArrayList<String> fileList;//附件名列表

    @Enumerated(EnumType.STRING)
    private ContractStatusEnum status;

    @CreatedDate
    private Timestamp createTime;

    @LastModifiedDate
    private Timestamp updateTime;


    private String formDataId;//表单id
}
