package com.honeywell.fireiot.schedule;

import com.honeywell.fireiot.entity.Contract;
import com.honeywell.fireiot.entity.ContractType;
import com.honeywell.fireiot.service.ContractService;
import com.honeywell.fireiot.service.ContractTypeService;
import com.honeywell.fireiot.utils.DateUtil;
import com.honeywell.fireiot.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:23 PM 3/14/2019
 */
@Service
@EnableScheduling
public class ContractSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger (ContractSchedule.class);

    @Autowired
    ContractTypeService contractTypeService;

    @Autowired
    WebSocketServer webSocketServer;


    @Autowired
    ContractService contractService;

    //@Scheduled( cron = "0 0/1 * * * ?")
    @Scheduled(cron = "0 0 14 * * ? ")  //每天下午两点发布
    public void  rinder(){

        //1、 获取所用开启查询状态的类别  List<ContractType> contractList = findByStatus( status == true);
        List<ContractType> contractTypeList = contractTypeService.findByStatus(true);
        if(contractTypeList.isEmpty()){
            return;
        }

        for(ContractType contractType: contractTypeList){
            Long  contractTypeId = contractType.getId();
            //获取类别下的合同
            List<Contract> contractList = contractService.findByContractType(contractType.getId());
            if(contractList.isEmpty()){
                continue;
            }

            List<Long> days =  contractType.getDays();
            //2、 根据 List<提前天数>+当天日期 = List<到期时间>
            List<Date> dates = DateUtil.getAfterListDate(days,"yyyy-MM-dd",true);
            if( (null == dates)  || (dates.isEmpty())){ //没有达到到期提醒
                continue;
            }

            int i = 0;
            for(Date date: dates){
                //3、查找对应类别下到期日期List<Contract>  contractList = findByContractTypeAndDeadline(....)
                List<Contract> contracts = contractService.findByContractTypeAndDeadLine(contractTypeId, date);
                if(contracts.isEmpty()){
                    i++;
                    continue;
                }
                for(Contract contract: contracts){
                    //编制消息= 合同+还有几天到期
                    String msg = "合同:"+contract.getName()+" 合同编号:"+contract.getContractNo()+" 还有"+days.get(i)+"天到期";

                    //发送对应的人
                    List<String> reminderId = contractService.findRedmindIds(contract);
                    if(StringUtils.isEmpty(reminderId)){
                        continue;
                    }
                    webSocketServer.sendMessageByUserIds(reminderId,msg );
                    System.out.println(contract.getDeadLine() + contract.getName());
                }

                i++;

            }

        }


        LOGGER.info ("合同到期提醒执行调度任务：" + new Date());
    }
}
