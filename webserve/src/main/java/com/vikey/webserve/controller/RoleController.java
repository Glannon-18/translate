package com.vikey.webserve.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.Role;
import com.vikey.webserve.service.IRoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-19
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService iRoleService;

    @GetMapping("/")
    public RespBean listAll() {
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.select("id", "name", "nameZh");
        List<Role> roleList = iRoleService.list(roleQueryWrapper);
        return RespBean.ok(roleList);
    }


}
