package com.middleware.camel.module.mysql.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @description: employee
 * @author: cuiweiman
 * @date: 2021/10/21 17:42
 */
@Data
@TableName("employee")
public class Employee {

    @TableId
    private String empId;

    private String empName;

}
