package com.middleware.camel.module.basic.helper;

import org.apache.camel.builder.RouteBuilder;

/**
 * @description: chapter01 helper
 * @author: cuiweiman
 * @date: 2021/10/21 11:46
 */
public class Chapter01Router extends RouteBuilder {
    @Override
    public void configure() {
        System.out.println("Hello Camel!");
    }
}
