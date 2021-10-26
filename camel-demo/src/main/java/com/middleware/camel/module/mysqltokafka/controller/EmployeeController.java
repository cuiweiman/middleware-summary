package com.middleware.camel.module.mysqltokafka.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.middleware.camel.module.mysqltokafka.mapper.EmployeeMapper;
import com.middleware.camel.module.mysqltokafka.model.Employee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: basic camel controller
 * @author: cuiweiman
 * @date: 2021/10/20 20:54
 */
@RestController
@Api(tags = "Employee管理模块")
@RequestMapping("/employee")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeController {

    private final EmployeeMapper mapper;

    @GetMapping("/test")
    @ApiOperation(value = "Swagger测试")
    @ApiImplicitParam(name = "name", value = "名称", required = true)
    public ResponseEntity<String> getByName(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok("name : " + name);
    }


    @PostMapping("/add")
    @ApiOperation(value = "新增雇员")
    @ApiImplicitParam(name = "employee", value = "雇员信息", required = true)
    public ResponseEntity<Boolean> addEmployee(@RequestBody Employee employee) {
        final int insert = mapper.insert(employee);
        return ResponseEntity.ok(1 == insert);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查看雇员")
    public ResponseEntity<List<Employee>> list() {
        final List<Employee> list = mapper.selectList(Wrappers.lambdaQuery());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除雇员")
    public ResponseEntity<Boolean> delete(@RequestParam(value = "empId") String empId) {
        final LambdaQueryWrapper<Employee> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(Employee::getEmpId, empId);
        final int delete = mapper.delete(lambdaQuery);
        return ResponseEntity.ok(1 == delete);
    }


}
