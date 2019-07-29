package com.honeywell.fireiot.service.impl;

import com.alibaba.fastjson.JSONException;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.honeywell.fireiot.service.MobMsgService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MobMsgServiceImpl implements MobMsgService {


    @Value("${tx.mobAppid}")
    private Integer mobAppid;

    @Value("${tx.mobAppkey}")
    private String mobAppkey;


    /*  // 短信应用SDK AppID
      static int appid = 1400161461;

      // 短信应用SDK AppKey
      static String appkey = "e9b0cfc77ff31afc24a2a2a775b652f1";
  */
    // 需要发送短信的手机号码
    private static String[] phoneNumbers = {"13701957952", "15005935831"};

    // 短信模板ID，需要在短信应用中申请
    private static int templateId = 231486;

    // 签名内容
    private static String smsSign = "霍尼韦尔";

    @Override
    public String sendSingleSms(String msg, String num) {
        SmsSingleSenderResult result = null;
        try {
            String[] params = {msg};
            SmsSingleSender ssender = new SmsSingleSender(mobAppid, mobAppkey);
            result = ssender.sendWithParam("86", num,
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();

        }
        return result.toString();
    }


    @Override
    public String sendMultiSms(String msg, String[] phoneNumbers) {
        String[] params = {msg};
        SmsMultiSender msender = new SmsMultiSender(mobAppid, mobAppkey);
        SmsMultiSenderResult result = null;
        // 签名参数未提供或者为空时，会使用默认签名发送短信
        try {
            result = msender.sendWithParam("86", phoneNumbers,
                    templateId, params, smsSign, "", "");
        } catch (HTTPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
