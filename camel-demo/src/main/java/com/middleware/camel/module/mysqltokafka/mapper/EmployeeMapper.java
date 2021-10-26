package com.middleware.camel.module.mysqltokafka.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.middleware.camel.module.mysqltokafka.model.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description: Employee mapper
 * @author: cuiweiman
 * @date: 2021/10/21 下午5:50
 */

public interface EmployeeMapper extends BaseMapper<Employee> {

    List<Employee> listEmployee();

}
