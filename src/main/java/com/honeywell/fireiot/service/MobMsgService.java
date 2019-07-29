package com.honeywell.fireiot.service;

import com.github.qcloudsms.httpclient.HTTPException;

public interface MobMsgService {

    String sendSingleSms(String msg, String num) throws HTTPException;
    String sendMultiSms(String msg, String[] phoneNumbers);

}

