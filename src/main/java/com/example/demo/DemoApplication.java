package com.example.demo;

import net.unicon.cas.client.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@SpringBootApplication//springboot启动类
@Configuration//配置扫描与ComponentScan一起使用
@ComponentScan(basePackages = {"com.example.demo"})//扫描此包下的所有component注解，定义ioc用以创建对象
@EnableAutoConfiguration
//@EnableCasClient
//@PropertySource(value = {"classpath:conf.properties"},ignoreResourceNotFound = true)//载入配置文件
public class DemoApplication extends SpringBootServletInitializer  {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DemoApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.currentTimeMillis();
    }
}
