package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSONObject;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;

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


}
