package com.middleware.minio.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: cuiweiman
 * @date: 2021/8/27 11:23
 */
@RestController
@Api(tags = "系统模块")
public class SystemController {

    @GetMapping("/getSystem")
    @ApiOperation(value = "获取系统信息")
    @ApiImplicitParam(name = "systemName", value = "系统名称", required = true)
    public ResponseEntity<String> getSystemByName(@RequestParam(value = "systemName") String systemName) {

        return ResponseEntity.ok("SystemName : " + systemName);
    }
}
