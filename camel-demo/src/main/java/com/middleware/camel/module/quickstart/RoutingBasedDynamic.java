package com.middleware.camel.module.quickstart;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 传递参数中，包含了 Camel 路由的 Endpoint 信息，直接被 Camel 解析并动态分发（ExchangeId不同）
 * <p class="code>
 * {
 * "data": {
 * "routeName":["direct:directRouteB"]
 * 或
 * "routeName":["direct:directRouteB","direct:directRouteB"]
 * }
 * }
 * </p>
 *
 * @description: 动态路由编排：传递的信息数据中，包含了路由信息，解析出路由信息后进行动态分发
 * @author: cuiweiman
 * @date: 2021/10/22 19:55
 */
@Slf4j
public class RoutingBasedDynamic {

    public static void main(String[] args) throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        camelContext.addRoutes(new RoutingBasedDynamic().new DirectRouteA());
        camelContext.addRoutes(new RoutingBasedDynamic().new DirectRouteB());
        camelContext.addRoutes(new RoutingBasedDynamic().new DirectRouteC());
        Thread.currentThread().join();
    }

    public class DirectRouteA extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("jetty:http://0.0.0.0:8282/dynamic")
                    // 配置 数据传播模式
                    .setExchangePattern(ExchangePattern.InOnly)
                    // 配置 数据接受者列表：根据json表达式从信息数据中解析
                    .recipientList().jsonpath("$.data.routeName").delimiter(",")
                    .end()
                    .process(new OtherProcessor());
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
            from("direct:directRouteC")
                    .to("log:DirectRouteC?showExchangeId=true");
        }
    }

    public class OtherProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            final Message in = exchange.getIn();
            // log.info("【OtherProcessor】的 exchange：{}", exchange);
            String content = in.getBody().toString();
            if (ExchangePattern.InOut == exchange.getPattern()) {
                final Message message = exchange.getMessage();
                message.setBody(content.concat(" == 被OtherProcessor处理过 == "));
            }
        }
    }

}
