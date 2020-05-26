package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.Fast_task;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.service.IFast_taskService;
import com.vikey.webserve.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/fast_task")
public class Fast_taskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Fast_taskController.class);

    @Resource
    private IFast_taskService IFast_taskService;

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    @PostMapping("/")
    public RespBean postFast_task(@RequestBody String jsonStr) {
//        original_text: this.original,
//        translate_text: this.translation,
//        original_language: this.language_ori,
//        translate_language: 'zh',
        LOGGER.debug(jsonStr);
        User user = SecurityUtils.getCurrentUser();

        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        LocalDateTime now = LocalDateTime.now();

        Fast_task fast_task = new Fast_task();
        fast_task.setCreate_time(now);
        fast_task.setOriginal_text(jsonObject.getString("original_text"));
        fast_task.setOriginal_language(jsonObject.getString("original_language"));
        fast_task.setTranslate_text(jsonObject.getString("translate_text"));
        fast_task.setTranslate_language(jsonObject.getString("translate_language"));
        fast_task.setName(creatNameBytime(now));
        fast_task.setUid(user.getId());

        IFast_taskService.save(fast_task);

        return RespBean.ok("ok");
    }

    private String creatNameBytime(LocalDateTime localDateTime) {
        return Constant.FAST_TASK_NAME_PREFIX + dtf.format(localDateTime);
    }


}
