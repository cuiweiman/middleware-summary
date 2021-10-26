package com.middleware.camel.module.quickstart;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.MulticastDefinition;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: 静态路由编排：将一个 Exchange 对象复制多份[Exchange对象是不同的]，发送给 CamelContext 编排的不同的接收者
 * @author: cuiweiman
 * @date: 2021/10/22 19:47
 */
@Slf4j
public class RoutingBasedStatic extends RouteBuilder {

    public static void main(String[] args) throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        camelContext.addRoutes(new RoutingBasedStatic());
        Thread.currentThread().join();
    }

    @Override
    public void configure() throws Exception {
        final ExecutorService executor = new ThreadPoolExecutor(5, 5, 1,
                TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(),
                new BasicThreadFactory.Builder().namingPattern("Thread-StaticRoute").build());
        final MulticastDefinition multicast = from("jetty:http://0.0.0.0:8282/multicast").multicast();
        // 设置并发执行:向多个 接收者 发送 Exchange 时
        multicast.setParallelProcessing("true");
        multicast.setExecutorService(executor);

        multicast.to(
                "log:helloWorld1?showExchangeId=true",
                "log:helloWorld2?showExchangeId=true"
        )
                .end()
                .process(new OtherProcessor());

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
