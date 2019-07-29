package com.honeywell.fireiot.integrationService.emq;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:13 PM 6/5/2018
 */
public class MqttInboundProperties {
    private String url;
    private String username;
    private String password;
    private String clientId;
    private String topicPrefix;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }
}
