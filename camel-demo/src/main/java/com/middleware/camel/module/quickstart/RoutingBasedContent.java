package com.middleware.camel.module.quickstart;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.jsonpath.JsonPathExpression;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * GET 请求： http://0.0.0.0:8282/choiceProcessor
 * 并添加 body json：
 * <p class="code">
 * {
 * "data": {
 * "orgId": "SYY"
 * }
 * }
 * </p>
 *
 * @description: 基于内容判断，进行处理器选择
 * @author: cuiweiman
 * @date: 2021/10/22 17:19
 */
public class RoutingBasedContent extends RouteBuilder {

    public static void main(String[] args) throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        camelContext.addRoutes(new RoutingBasedContent());
        Thread.currentThread().join();
    }

    @Override
    public void configure() throws Exception {
        // JsonPath 表达式，从 Http body 的 JSON 数据中，提取 data.orgId 的属性值
        final JsonPathExpression jsonPathExpression = new JsonPathExpression("$.data.orgId");
        jsonPathExpression.setResultType(String.class);

        from("jetty:http://0.0.0.0:8282/choiceProcessor")
                // 先送如 HttpProcessor，将流转化成字符串，即使不转，choice也可以进行
                .process(new HttpProcessor())
                // 将orgId属性的值存储 exchange in Message的header中，以便后续进行判断
                .setHeader("orgId", jsonpath("$.data.orgId"))
                // 按内容选择处理器
                .choice()
                .when(header("orgId").isEqualTo("SYY")).process(new OtherProcessor())
                .when(header("orgId").isEqualTo("CWM")).process(new OtherProcessor2())
                .otherwise().process(new OtherProcessor3())
                // 结束 按内容选择
                .endChoice();
    }

    /**
     * HttpProcessor 流转字符串
     */
    public class HttpProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            final Message message = exchange.getIn();
            try (InputStream inputStream = (InputStream) message.getBody()) {
                String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                if (ExchangePattern.InOut == exchange.getPattern()) {
                    final Message msg = exchange.getMessage();
                    msg.setBody(content);
                }
            }
        }
    }

    public class OtherProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            final Message in = exchange.getIn();
            String content = in.getBody().toString();
            if (ExchangePattern.InOut == exchange.getPattern()) {
                final Message msg = exchange.getMessage();
                msg.setBody(content.concat(" 【OtherProcessor处理】 "));
            }
        }
    }


    public class OtherProcessor2 implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            final Message in = exchange.getIn();
            String content = in.getBody().toString();
            if (ExchangePattern.InOut == exchange.getPattern()) {
                final Message msg = exchange.getMessage();
                msg.setBody(content.concat(" == OtherProcessor2 处理 == "));
            }
        }
    }


    public class OtherProcessor3 implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            final Message in = exchange.getIn();
            String content = in.getBody().toString();
            if (ExchangePattern.InOut == exchange.getPattern()) {
                final Message msg = exchange.getMessage();
                msg.setBody(content.concat(" || OtherProcessor3 处理 || "));
            }
        }
    }


}
