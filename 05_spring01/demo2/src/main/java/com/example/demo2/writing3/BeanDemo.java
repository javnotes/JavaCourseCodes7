package com.example.demo2.writing3;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author: luf
 * @create: 2021-12-12 23:44
 **/
@Component
public class BeanDemo {
    public BeanDemo() {
        System.out.println("无参构造函数执行了");
    }

    public void method() {
        System.out.println("自定义方法执行了");
    }
}
