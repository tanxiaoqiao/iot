package com.honeywell.fireiot.constant;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 4:43 PM 3/27/2019
 */
public enum MaterialOperateType {
    MATERIAL_IN,//物资入库
    MATERIAL_OUT,//物资出库
    MATERIAL_RESERVE_OUT,//物资预定出库
    MATERIAL_BACK,//物资退库
    MATERIAL_RESERVE,//物资预定
    MOVE_STOCK,//移库
    IN_STOCK,//移入库
    OUT_STOCK,//移出库
    MATERIAL_CHECK,//盘点物资

    ;
}
