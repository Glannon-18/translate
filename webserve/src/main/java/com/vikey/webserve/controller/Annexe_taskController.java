package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.Constant;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.*;
import com.vikey.webserve.service.IAnnexeService;
import com.vikey.webserve.service.IAnnexe_taskService;
import com.vikey.webserve.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

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


    @Resource
    private PersonalConfig personalConfig;


    @PostMapping("/")
    public RespBean createAnnexe_Task(@RequestBody String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        IAnnexe_taskService.createAnnexe_task(jsonObject);
        return RespBean.ok("ok");
    }

    @GetMapping("/{id}")
    public RespBean getAnnext_Task(@PathVariable String id) {
        QueryWrapper<Annexe_task> queryWrapper = new QueryWrapper();
        queryWrapper.select("id", "name").eq("id", Long.valueOf(id));
        List<Annexe_task> annexe_tasks = IAnnexe_taskService.getBaseMapper().selectList(queryWrapper);
        return RespBean.ok("ok", annexe_tasks.get(0));
    }


    @GetMapping("/listByDate")
    public RespBean getAnnexe_TaskListByDate(@RequestParam String name) {
        LOGGER.debug(name);
        User user = SecurityUtils.getCurrentUser();
        Map map = IAnnexe_taskService.getAnnexe_taskByDate(user.getId(), name);
        return RespBean.ok("ok", map);
    }

    @PostMapping("/upload")
    public RespBean upload(MultipartFile multipartFile) {

        Map<String, String> map = new HashMap<>();
        String name = multipartFile.getOriginalFilename();
        String dirPath = personalConfig.getUpload_dir();
        String filePath = dirPath + File.separator + UUID.randomUUID().toString().substring(0, 12) + "_" + multipartFile.getOriginalFilename();
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("originalName", name);
        map.put("severName", filePath.replace(dirPath + File.separator, ""));
        return RespBean.ok("ok", map);
    }

}
