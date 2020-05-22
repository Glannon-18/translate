package com.vikey.webserve.controller;


import com.vikey.webserve.entity.RespBean;
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

    @GetMapping("/a")
    public String a() {
        return "aaa";
    }

    @GetMapping("/b")
    public String b() {
        return "bbb";
    }


}
