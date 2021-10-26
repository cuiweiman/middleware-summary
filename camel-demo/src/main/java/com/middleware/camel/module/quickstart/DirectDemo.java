package com.middleware.camel.module.quickstart;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 日志打印内容：由ExchangeID可得知，是同一个Exchange对象
 * 16:36:06.860 [qtp1920907467-14] INFO DirectRouteB - Exchange[Id: 2F69791D2465DC6-0000000000000000, ExchangePattern: InOut, BodyType: org.apache.camel.converter.stream.InputStreamCache, Body: [Body is instance of org.apache.camel.StreamCache]]
 * 16:36:06.861 [qtp1920907467-14] INFO DirectRouteA - Exchange[Id: 2F69791D2465DC6-0000000000000000, ExchangePattern: InOut, BodyType: org.apache.camel.converter.stream.
 *
 * @description: Endpoint Direct 发送同一个 Exchange 对象到不同的 路由中
 * @author: cuiweiman
 * @date: 2021/10/22 15:32
 */
public class DirectDemo {

    public static void main(String[] args) throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        camelContext.addRoutes((new DirectDemo()).new DirectRouteA());
        camelContext.addRoutes((new DirectDemo()).new DirectRouteB());
        camelContext.addRoutes((new DirectDemo()).new DirectRouteC());
        Thread.currentThread().join();
    }

    public class DirectRouteA extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("jetty:http://0.0.0.0:8282/directCamel")
                    .to("direct:directRouteB")
                    .to("direct:whatever,keep same is ok")
                    .to("log:DirectRouteA?showExchangeId=true");
        }
    }

    public class DirectRouteB extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("direct:directRouteB")
                    .to("log:DirectRouteB?showExchangeId=true");
        }
    }

    public class DirectRouteC extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("direct:whatever,keep same is ok")
                    .to("log:DirectRouteC?showExchangeId=true");
        }
    }

}
