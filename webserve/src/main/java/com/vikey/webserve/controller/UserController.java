package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.RespPageBean;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.service.IUserService;
import com.vikey.webserve.utils.VerificationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
        return RespBean.ok("添加用户成功！");
    }

    @GetMapping("/")
    public RespPageBean listUser(@RequestParam String name, @RequestParam Integer currentPage) {
        Page<User> page = new Page<>(currentPage, Constant.PAGESIZE);
        IPage<User> userIPage = iUserService.selectUserWithRolesByName(page, name);
        return new RespPageBean(userIPage.getTotal(), userIPage.getRecords(), userIPage.getSize());
    }

    @GetMapping("/{id}")
    public RespBean getUser(@PathVariable String id) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("id", "account", "username", "telephone").eq("id", Long.valueOf(id));
        return RespBean.ok(iUserService.getOne(userQueryWrapper));
    }

    @PutMapping("/{id}")
    public RespBean updateUser(@PathVariable String id, @RequestBody JSONObject jsonObject) {
        iUserService.update(id, jsonObject);
        return RespBean.ok("修改用户成功！");
    }


    @DeleteMapping("/{id}")
    public RespBean deleteUser(@PathVariable String id) {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("discard", Constant.DELETE).eq("id", Long.valueOf(id));
        iUserService.update(userUpdateWrapper);
        return RespBean.ok("删除用户成功！");

    }

    @GetMapping("/check")
    public RespBean checkeAccount(@RequestParam String account, @RequestParam String userid) {
        Integer count = iUserService.countByAccount(account, StringUtils.isEmpty(userid) ? null : Long.valueOf(userid));
        return RespBean.ok(count);

    }

    @GetMapping("/verifyCode")
    public void verifyCode(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        VerificationCode code = new VerificationCode();
        BufferedImage image = code.getImage();
        String text = code.getText();
        HttpSession session = request.getSession(true);
        session.setAttribute("verify_code", text);
        VerificationCode.output(image,resp.getOutputStream());
    }


}
