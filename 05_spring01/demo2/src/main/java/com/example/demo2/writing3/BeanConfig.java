package com.example.demo2.writing3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author: luf
 * @create: 2021-12-12 23:46
 **/
@Configuration
@ComponentScan(basePackageClasses = {BeanDemo.class})
public class BeanConfig {
}
