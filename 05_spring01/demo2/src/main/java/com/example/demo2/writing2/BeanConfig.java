package com.example.demo2.writing2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建配置类
 * 方法返回类型为 BeanDemo
 *
 * @author: luf
 * @create: 2021-12-12 23:06
 **/

@Configuration
public class BeanConfig {

    @Bean(name = "javaCodeWriting")
    public BeanDemo newBean() {
        return new BeanDemo();
    }

}
