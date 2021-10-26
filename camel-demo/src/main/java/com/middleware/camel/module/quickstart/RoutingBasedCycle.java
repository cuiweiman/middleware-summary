package com.middleware.camel.module.quickstart;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: Dynamic Route 循环路由：路由分发到下一个端点，断点处理完成后，再次返回到路由进行分发，直到无分支可选
 * @author: cuiweiman
 * @date: 2021/10/25 10:44
 */
@Slf4j
public class RoutingBasedCycle {
    public static void main(String[] args) throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        camelContext.addRoutes(new RoutingBasedCycle().new RouteBuilderA());
        camelContext.addRoutes(new RoutingBasedCycle().new DirectRouteB());
        camelContext.addRoutes(new RoutingBasedCycle().new DirectRouteC());
        Thread.currentThread().join();
    }

    public class RouteBuilderA extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("jetty:http://0.0.0.0:8282/cycle")
                    .dynamicRouter().method(this, "doDirect")
                    .process(new OtherProcessor());
        }

        /**
         * 该方法用于根据“动态循环”的次数，确定下一个执行的Endpoint
         *
         * @param properties 通过注解能够获得的Exchange中properties属性，可以进行操作，并反映在整个路由过程中
         * @return Direct Route Endpoint
         */
        public String doDirect(@ExchangeProperties Map<String, Object> properties) {
            AtomicInteger time = (AtomicInteger) properties.get("time");
            if (time == null) {
                time = new AtomicInteger(0);
                properties.put("time", time);
            }
            log.info("这是Dynamic Router循环第：【" + time.incrementAndGet() + "】次执行！执行线程：" + Thread.currentThread().getName());
            if (time.get() == 1) {
                return "direct:directRouteB";
            } else if (time.get() == 2) {
                return "direct:directRouteC";
            } else if (time.get() == 3) {
                return "log:directRouteA?showExchangeId=true&showProperties=true&showBody=false";
            }
            return null;
        }
    }

    public class DirectRouteB extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("direct:directRouteB")
                    .to("log:DirectRouteB?showExchangeId=true&showProperties=true&showBody=false");
        }
    }

    public class DirectRouteC extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("direct:directRouteC")
                    .to("log:DirectRouteC?showExchangeId=true&showProperties=true&showBody=false");
        }
    }

    public class OtherProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            final Message in = exchange.getIn();
            log.info("【OtherProcessor】的 exchange：{}", exchange);
            String content = in.getBody().toString();
            if (ExchangePattern.InOut == exchange.getPattern()) {
                final Message message = exchange.getMessage();
                message.setBody(content.concat(" == 被OtherProcessor处理过 == "));
            }
        }
    }

}
