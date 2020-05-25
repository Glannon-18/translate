package com.vikey.webserve.controller;


import com.vikey.webserve.entity.RespBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    private static final Logger LOGGER= LoggerFactory.getLogger(UserController.class);

    @GetMapping("/a")
    public String a() {
        return "aaa";
    }

    @GetMapping("/b")
    public String b() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        LOGGER.info(authentication.toString());
        return "bbb";
    }


}
