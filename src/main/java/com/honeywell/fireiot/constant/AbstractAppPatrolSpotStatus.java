package com.honeywell.fireiot.constant;

import lombok.NoArgsConstructor;

/**
 * @project: fire-foxconn-back-polling
 * @name: AbstractAppPatrolSpotStatus
 * @author: dexter
 * @create: 2019-04-08 18:20
 * @description:
 **/
@NoArgsConstructor
public abstract class AbstractAppPatrolSpotStatus {

    public static final int FAULT_NEED_TO_BE_FIXED = 2;

    public static final int SPOT_NORMAL = 1;

    public static final int SPOT_ABNORMAL = 0;
}
