package com.honeywell.fireiot.dto;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:03 PM 5/30/2018
 */
public class LoginSearch {
    private int pi=1;//pi

    private int ps =10;//ps

    private String keyword;

    public int getPi() {
        return pi;
    }

    public void setPi(int pi) {
        this.pi = pi;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "LoginSearch{" +
                "pi=" + pi +
                ", ps=" + ps +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
