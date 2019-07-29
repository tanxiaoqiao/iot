package com.honeywell.fireiot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: Kayla,Ye
 * @Description: 通过url可访问外部资源
 * @Date:Created in 4:19 PM 7/24/2018
 */
@Configuration
public class WebFileurlConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //addResourceHandler是指你想在url请求的路径
        //addResourceLocations是文件存放的真实路径

        registry.addResourceHandler("/plandata/**").addResourceLocations("file:./plandata/");
        super.addResourceHandlers(registry);
    }
}
