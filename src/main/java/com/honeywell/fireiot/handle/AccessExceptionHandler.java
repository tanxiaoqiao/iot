package com.honeywell.fireiot.handle;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.utils.ResponseObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@ResponseBody
public class AccessExceptionHandler {


    /**
     * 统一处理所有未处理异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject handleGlobalException(Exception e) {
        e.printStackTrace();
        ResponseObject responseObject = ResponseObject.fail(ErrorEnum.SERVER_ERROR);
        return responseObject;
    }

    /**
     * 自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = {BusinessException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject handleRestException(BusinessException e) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCode(e.code);
        responseObject.setMsg(e.msg);
        return responseObject;
    }

    /**
     * 请求参数缺失
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject handleRestException(MissingServletRequestParameterException e) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCode(ErrorEnum.MISS_REQUEST_PARAMTER.getCode());
        responseObject.setMsg(e.getMessage());
        return responseObject;
    }


    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject handleIllegalParamException(BindException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        ResponseObject<Object> responseObject = new ResponseObject<>();
        if (errors != null && errors.size() > 0) {
            responseObject.setCode(40001);
            responseObject.setMsg(errors.get(0).getDefaultMessage());
        }
        return responseObject;
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject handleHttpMessageNotReadableException(HttpMessageConversionException e) {
        e.printStackTrace();
        return ResponseObject.fail(ErrorEnum.PARAMETER_ERROR);
    }

}
