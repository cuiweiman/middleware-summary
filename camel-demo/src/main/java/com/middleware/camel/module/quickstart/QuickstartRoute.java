package com.middleware.camel.module.quickstart;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @description: quick start for apache camel
 * @author: cuiweiman
 * @date: 2021/10/22 10:52
 */
public class QuickstartRoute extends RouteBuilder {

    public static void main(String[] args) throws Exception {
        // Camel 上下文对象，驱动路由
        final CamelContext camelContext = new DefaultCamelContext();
        // 启动路由
        camelContext.start();

        /*
         * 将 编排好的 消息路由 添加到 上下文环境中。
         * 这里先启动 上下文 之后，才添加路由，以此证明 Camel 支持动态 加载/卸载 路由；
         * 后续设计的 broker 需要依赖此功能。
         */
        camelContext.addRoutes(new QuickstartRoute());

        // 保证 主线程等待，不退出
        Thread.currentThread().join();
       /* synchronized (Chapter03Quickstart.class) {
            Chapter03Quickstart.class.wait();
        }*/

    }

    /**
     * 编排路由规则
     *
     * @throws Exception 异常
     */
    @Override
    public void configure() throws Exception {
        // 路由的消息入口：使用 HTTP 协议访问本物理机 127.0.0.1或IP:8282，都可以将携带的 HTTP 数据传送至 路由
        // postman 中以 get 请求from的端口，数据传输在 http body 中即可测试
        from("jetty:http://0.0.0.0:8282")
                // 消息 处理器
                .process(new HttpProcessor())
                // endPoint控制端点UI描述表示：Log4j的实现，消息最终会以Log日志的方式输出到控制台上
                .to("log:helloWorld?showExchangeId=true");
    }


    /**
     * 处理器：对输入的json格式进行转换
     * 处理器编排：从路由中获取到输入的JSON，处理后在经过路由输出
     */
    public class HttpProcessor implements Processor {

        @Override
        public void process(Exchange exchange) {
            Message message = exchange.getIn();
            try (InputStream is = (InputStream) message.getBody()) {
                String inputContext = IOUtils.toString(is, StandardCharsets.UTF_8);
                if (ExchangePattern.InOut == exchange.getPattern()) {
                    final Message outMessage = exchange.getMessage();
                    outMessage.setBody(inputContext.concat(" || out processor"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
