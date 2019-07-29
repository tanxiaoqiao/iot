package com.honeywell.fireiot.constant;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 1:57 PM 4/22/2019
 */
public enum MaterialOrderStatusEnum {
   WAIT_CONFIRM, //待审核
   REJECTED, //已驳回
   WAIT_MATERIAL_OUT,//待出库
   HAVE_MATERIAL_OUT,//已出库
   CANCEL_RESERVE,//取消预定
   CANCEL_MATERIAL_OUT,//取消出库
   ;
}
