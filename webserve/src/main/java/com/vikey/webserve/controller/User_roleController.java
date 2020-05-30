package com.vikey.webserve.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.User_role;
import com.vikey.webserve.service.IUser_roleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/user_role")
public class User_roleController {

    @Resource
    private IUser_roleService iUser_roleService;

    @GetMapping("/")
    public RespBean getRidByUid(@RequestParam String uid) {
        QueryWrapper<User_role> user_roleQueryWrapper = new QueryWrapper<>();
        user_roleQueryWrapper.select("rid").eq("uid", Long.valueOf(uid));
        List<User_role> user_roleList = iUser_roleService.list(user_roleQueryWrapper);
        //这里处理一下再回传，方便双向绑定
        Long[] ids=new Long[user_roleList.size()];
        for (int i=0;i<ids.length;i++){
            ids[i]=user_roleList.get(i).getRid();
        }
        return RespBean.ok("ok", ids);
    }

}
