package com.middleware.camel.module.mysqltokafka.route;

import com.middleware.camel.module.mysqltokafka.model.Employee;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: mysql route
 * @author: cuiweiman
 * @date: 2021/10/26 16:21
 */
@Component
public class MysqlCamelRoute extends RouteBuilder {
    @Override
    public void configure() {
        /*from("direct:select").to("").process(exchange -> {
            ArrayList<Map<String, String>> dataList = (ArrayList<Map<String, String>>) exchange.getIn().getBody();
            List<Employee> employees = new ArrayList<>();
            for (Map<String, String> data : dataList) {
                Employee employee = new Employee();
                employee.setEmpId(data.get("empId"));
                employee.setEmpName(data.get("empName"));
                employees.add(employee);
            }
            exchange.getIn().setBody(employees);
        });*/

        /*from("direct:insert").log("Processing message: ${body}")
                .setHeader("message", body()).process(exchange -> {
            Employee employee = exchange.getIn().getBody(Employee.class);
            Map<String, Object> answer = new HashMap<>();
            answer.put("empId", employee.getEmpId());
            answer.put("empName", employee.getEmpName());
            exchange.getIn().setBody(answer);
        }).to("sql:INSERT INTO employee(emp_id, emp_name) VALUES (:#empId, :#empName)");*/

    }
}
