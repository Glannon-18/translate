package com.vikey.webserve.controller;


import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.service.IAnnexe_taskService;
import com.vikey.webserve.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/annexe_task")
public class Annexe_taskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Annexe_taskController.class);

    @Resource
    private IAnnexe_taskService IAnnexe_taskService;

    @GetMapping("/listByDate")
    public RespBean getFast_TaskListByDate(@RequestParam String name) {
        LOGGER.debug(name);
        User user = SecurityUtils.getCurrentUser();
        Map map = IAnnexe_taskService.getAnnexe_taskByDate(user.getId(), name);
        return RespBean.ok("ok", map);
    }

}
