package com.honeywell.fireiot.utils;


import com.honeywell.fireiot.constant.ErrorEnum;

public class ResponseObject<T> {
    private int code;
    private String msg;

    private T data;

    @Override
    public String toString() {
        return "ResponseObject{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static <T> ResponseObject<T> fail(ErrorEnum errorCode) {
        ResponseObject<T> responseObject = new ResponseObject<>();
        responseObject.setCode(errorCode.getCode());
        responseObject.setMsg(errorCode.getMsg());
        return responseObject;
    }
    public static <T> ResponseObject<T> success(T data) {
        ResponseObject<T> respObject = new ResponseObject<>();
        respObject.setCode(200);
        respObject.setMsg("处理成功");
        respObject.setData(data);
        return respObject;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
