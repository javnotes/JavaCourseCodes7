package com.example.demo2.writing3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: luf
 * @create: 2021-12-12 23:56
 **/
@Component
public class BeanMain {
    @Autowired
    private BeanDemo demo;
    public static void main(String[] args) {
        demo.method();
    }
}
