package com.middleware.camel.module.basic;

import com.middleware.camel.module.basic.helper.Chapter01Router;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * @description: hello world
 * @author: cuiweiman
 * @date: 2021/10/21 11:46
 */
public class Chapter01Hello {
    public static void main(String[] args) throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new Chapter01Router());
        camelContext.start();
    }
}
