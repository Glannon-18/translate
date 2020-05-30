package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.RespPageBean;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    @Resource
    private IUserService iUserService;


    @PostMapping("/")
    public RespBean createUser(@RequestBody JSONObject jsonObject) {
        LOGGER.info(jsonObject.toJSONString());
        iUserService.create(jsonObject);
        return RespBean.ok("ok");
    }

    @GetMapping("/")
    public RespPageBean list(@RequestParam String name,@RequestParam Integer currentPage) {
        Page<User> page = new Page<>(currentPage, Constant.PAGESIZE);
        IPage<User> userIPage=iUserService.selectUserWithRolesByName(page,name);
        return new RespPageBean(userIPage.getTotal(),userIPage.getRecords(),userIPage.getSize());
    }


}
