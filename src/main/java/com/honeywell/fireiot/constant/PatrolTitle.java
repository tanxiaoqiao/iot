package com.honeywell.fireiot.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 10:10 AM 1/24/2019
 */
public class PatrolTitle {

    public static final String PATROL_NAME = "巡检任务";
    public static final String PATROL_PRE_DATE = "预计开始日期";


    public static final String PATROL_PRE_ENDDATE = "预计完成日期";


    public static final String PATROL_ACT_DATE = "实际开始日期";


    public static final String PATROL_ACT_ENDDATE = "实际完成日期";


    public static final String PATROL_STATUS = "巡检状态";


    public static final List<String> PATROL_LIST = Arrays.asList(PATROL_NAME, PATROL_PRE_DATE
            , PATROL_PRE_ENDDATE, PATROL_ACT_DATE
            , PATROL_ACT_ENDDATE, PATROL_STATUS);

}
