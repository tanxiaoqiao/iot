package com.honeywell.fireiot.exception;

import com.honeywell.fireiot.constant.ErrorEnum;

/**
 * 所有业务请求异常父类
 *
 * @author: xiaomingCao
 * @create: 5/18/2018
 */
public class BusinessException extends RuntimeException {
    public int code;
    public String msg;


    public BusinessException(ErrorEnum e) {
        this.code = e.getCode();
        this.msg = e.getMsg();
    }
}
