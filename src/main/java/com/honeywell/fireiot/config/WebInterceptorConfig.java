package com.honeywell.fireiot.config;

import com.honeywell.fireiot.interceptor.EnvInterceptor;
import com.honeywell.fireiot.interceptor.PageInterceptor;
import com.honeywell.fireiot.interceptor.TraceLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @Author: Kris
 * @Description: 拦截器配置类
 * @Date: 5/23/2018 10:02 AM
 */
@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {

    @Bean
    PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

    @Bean
    TraceLogInterceptor traceLogInterceptor() {
        return new TraceLogInterceptor();
    }

    @Bean
    EnvInterceptor envInterceptor() {
        return new EnvInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pageInterceptor()).addPathPatterns("/**").order(1);
        registry.addInterceptor(envInterceptor()).addPathPatterns("/**").order(2);
        registry.addInterceptor(traceLogInterceptor()).addPathPatterns("/**").order(3);
    }
}
