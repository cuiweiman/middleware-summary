package com.middleware.camel.module.mysql.controller;

import com.middleware.camel.module.mysql.vo.EmployeeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: basic camel controller
 * @author: cuiweiman
 * @date: 2021/10/20 20:54
 */
@Slf4j
@RestController
@Api(tags = "camel数据管理模块")
@RequestMapping("/data")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CamelController {

    private final ProducerTemplate producerTemplate;

    @GetMapping("/list")
    @ApiOperation(value = "查看雇员")
    public ResponseEntity<List> list() {
        final List list = producerTemplate.requestBody("direct:select", null, List.class);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增雇员")
    @ApiImplicitParam(name = "employeeVO", value = "雇员信息", required = true)
    public ResponseEntity<Boolean> addEmployee(@RequestBody EmployeeVO employeeVO) {
        final List list = producerTemplate.requestBody("direct:insert", employeeVO, List.class);
        log.info(list.toString());
        return ResponseEntity.ok(true);
    }

}
