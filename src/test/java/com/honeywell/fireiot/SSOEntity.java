package com.honeywell.fireiot;

import lombok.Data;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/28 7:31 PM
 */
@Data
public class SSOEntity {
    private String ip;
    private String password;
    private int resource;
    private String userName;
}
