package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.*;
import com.vikey.webserve.service.IAnnexe_taskService;
import com.vikey.webserve.service.IAsyncService;
import com.vikey.webserve.service.IFast_taskService;
import com.vikey.webserve.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
    private IAnnexe_taskService iAnnexe_taskService;

    @Resource
    private IFast_taskService iFast_taskService;


    @Resource
    private PersonalConfig personalConfig;

    @Resource
    private IAsyncService iAsyncService;


    @PostMapping("/")
    public RespBean createAnnexe_Task(@RequestBody String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String language = jsonObject.getString("language");
        String language_tra = jsonObject.getString("language_tra");
        List<Annexe> annexes = iAnnexe_taskService.createAnnexe_task(jsonObject);
        iAsyncService.translate(annexes, language, language_tra);
        return RespBean.ok("创建文本任务成功");
    }

    @GetMapping("/{id}")
    public RespBean getAnnexe_Task(@PathVariable String id) {
        QueryWrapper<Annexe_task> queryWrapper = new QueryWrapper();
        queryWrapper.select("id", "name", "original_language", "translate_language").eq("id", Long.valueOf(id));
        Annexe_task annexe_task = iAnnexe_taskService.getBaseMapper().selectOne(queryWrapper);
        return RespBean.ok(annexe_task);
    }


    @GetMapping("/listByDate")
    public RespBean getAnnexe_TaskListByDate(@RequestParam String name) {
        LOGGER.debug(name);
        User user = SecurityUtils.getCurrentUser();
        Map map = iAnnexe_taskService.getAnnexe_taskByDate(user.getId(), name);
        return RespBean.ok(map);
    }

    @PostMapping("/upload")
    public RespBean upload(MultipartFile multipartFile) {

        Map<String, String> map = new HashMap<>();
        String name = multipartFile.getOriginalFilename();
        String type = name.split("\\.")[1];
        String dirPath = personalConfig.getUpload_dir();
        String filePath = dirPath + File.separator + UUID.randomUUID().toString() + "." + type;
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("type", type);
        map.put("originalName", name);
        map.put("severName", filePath.replace(dirPath + File.separator, ""));
        return RespBean.ok(map);
    }

    @GetMapping("/getAllInfo")
    public RespBean getAllInfo(@RequestParam String type) {
        LocalDateTime after = null;
        if ("24h".equals(type)) {
            after = LocalDateTime.now().minusHours(24l);
        } else if ("30d".equals(type)) {
            after = LocalDateTime.now().minusDays(30l);
        } else if ("all".equals(type)) {
//            取两个任务表的时间最小值，取两项最小的
            LocalDateTime at_min = iAnnexe_taskService.minDateTime();
            LocalDateTime ft_min = iFast_taskService.minDateTime();
            after = at_min.compareTo(ft_min) <= 0 ? at_min : ft_min;
        } else {
            return RespBean.ok();
        }
        List<Map> result = iAnnexe_taskService.getAllInfo(after);
        return RespBean.ok(result);
    }


    @GetMapping("/getLanguageShare")
    public RespBean getLanguageShare(@RequestParam String type) {
        LocalDateTime after = null;
        if ("24h".equals(type)) {
            after = LocalDateTime.now().minusHours(24l);
        } else if ("30d".equals(type)) {
            after = LocalDateTime.now().minusDays(30l);
        } else if ("all".equals(type)) {
            after = null;
        } else {
            return RespBean.ok();
        }
        List<Map<String, String>> result = iAnnexe_taskService.getCountByLanguage(after);
        return RespBean.ok(result);
    }

}
