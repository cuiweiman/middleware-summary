package com.middleware.camel.module.mysql.route;

import com.middleware.camel.module.mysql.model.Employee;
import com.middleware.camel.module.mysql.vo.EmployeeVO;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ProcessorDefinition#bean(Object, String)} 注意Bean方法在路由中的处理顺序
 *
 * @description: employee route
 * @author: cuiweiman
 * @date: 2021/10/26 16:21
 */
@Component
public class EmployeeRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:select")
                .bean("employeeMapper", "selectList")
                .process(exchange -> {
                    List<Employee> dataList = (List<Employee>) exchange.getIn().getBody();
                    List<EmployeeVO> employeeVOS = new ArrayList<>();
                    for (Employee data : dataList) {
                        EmployeeVO employeeVO = new EmployeeVO();
                        employeeVO.setId(data.getEmpId());
                        employeeVO.setName(data.getEmpName());
                        employeeVOS.add(employeeVO);
                    }
                    exchange.getIn().setBody(employeeVOS);
                });

        from("direct:insert").log("Processing message: ${body}")
                .process(exchange -> {
                    EmployeeVO employeeVO = exchange.getIn().getBody(EmployeeVO.class);
                    Employee employee = new Employee();
                    employee.setEmpId(employeeVO.getId());
                    employee.setEmpName(employeeVO.getName());
                    exchange.getMessage().setBody(employee);
                })
                .bean("employeeMapper", "insert");
    }
}
