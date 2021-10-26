package com.middleware.camel.module.basic;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

/**
 * RouteBuilder#from()：起始节点, url?delay=5000 表示每5秒轮训一次文件夹内是否有文件
 * ProcessorDefinition#process()：处理器逻辑
 * ProcessorDefinition#to()：目标节点
 * <p>
 * 实现功能：将 from 目录下的所有文件，移动到 to 目录下，处理器中负责打印文件内容。
 *
 * @description: file copy
 * @author: cuiweiman
 * @date: 2021/10/21 14:12
 */
public class Chapter02FileCopy {

    public static void main(String[] args) {
        try {
            String from = "file:/Users/cuiweiman/Desktop";
            final CamelContext camelContext = new DefaultCamelContext();

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    this
                            .from(from.concat("?delay=5000"))
                            .process(exchange -> {
                                // 处理器
                                GenericFile<File> gf = exchange.getIn().getBody(GenericFile.class);
                                File file = gf.getFile();
                                final String content = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8.name());
                                System.out.println(content);

                            })
                            .to(from.concat("/config"));
                }
            });

            camelContext.start();
            //防止主线程退出
            Object object = new Object();
            synchronized (object) {
                object.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
