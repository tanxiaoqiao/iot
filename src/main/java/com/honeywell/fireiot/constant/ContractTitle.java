package com.honeywell.fireiot.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 10:10 AM 1/24/2019
 */
public class ContractTitle {

    public static final String CONTRACT_NO = "合同编号";

    public static final String CONTRACT_NAME = "合同名称";

    public static final String CONTRACT_TYPE = "合同类型";

    public static final String CONTRACT_MONEY = "合同金额";

    public static final String SALEMAN = "业务员";

    public static final String PAYTYPE = "收付类型";

    public static final String SIGNDATE = "签订日期";

    public static final String STARTDATE = "开始日期";

    public static final String DEADLINE = "截至日期";

    public static final String CONTRACT_STATUS = "合同状态";

    public static final List<String> CONTRACT_TITLELIST = Arrays.asList(CONTRACT_NO, CONTRACT_NAME,
            CONTRACT_TYPE, CONTRACT_MONEY, SALEMAN, PAYTYPE,SIGNDATE, STARTDATE,DEADLINE,CONTRACT_STATUS);

}
