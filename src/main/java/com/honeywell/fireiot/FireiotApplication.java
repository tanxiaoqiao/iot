package com.honeywell.fireiot;

import com.honeywell.fireiot.integrationService.emq.MqttProperties;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
  * @Author: Kris
  * @Description: 
  * @Date: 5/15/2018 8:49 PM
  */ 
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableTransactionManagement
@ServletComponentScan
@EnableWebSocket
@EnableConfigurationProperties({MqttProperties.class})
@EnableJpaAuditing
@EnableAsync
@EnableSwagger2
public class FireiotApplication {

    public static void main(String[] args) {
        SpringApplication.run(FireiotApplication.class, args);
    }

}
