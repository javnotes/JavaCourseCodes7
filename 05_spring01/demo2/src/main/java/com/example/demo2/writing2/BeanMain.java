package com.example.demo2.writing2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: luf
 * @create: 2021-12-12 23:21
 **/
public class BeanMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class);
        BeanDemo demo = (BeanDemo) context.getBean("javaCodeWriting");
        demo.method();
    }
}
