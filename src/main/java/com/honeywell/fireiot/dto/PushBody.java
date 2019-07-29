package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.HashMap;

/**
 * @Author: Kris
 * @Date: 2019-06-21  13:16
 */
@Data
public class PushBody {
    private String topic;
    private String[] users;
    private String body;
    private  Integer type;
    private String title;
   // private HashMap extra;



    public PushBody(String title,String topic, String[] users, String body, Integer type) {
        this.title = title;
        this.topic = topic;
        this.users = users;
        this.body = body;
        this.type = type;
    }

    public PushBody() {
    }
}
