package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSONObject;
import com.vikey.webserve.Constant;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Fast_task;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.service.IFast_taskService;
import com.vikey.webserve.service.TranslateService;
import com.vikey.webserve.utils.LanguageUtils;
import com.vikey.webserve.utils.SecurityUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private IFast_taskService iFast_taskService;

    @Resource
    private PersonalConfig personalConfig;

    @Resource(name = "xiaoNiuTranslateService")
    private TranslateService translateService_xiaoniu;

    @Resource(name = "pingSoftTranslateService")
    private TranslateService translateService_pingsoft;

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    @PostMapping("/")
    public RespBean postFast_task(@RequestBody JSONObject jsonObject) {
        LOGGER.debug(jsonObject.toJSONString());
        User user = SecurityUtils.getCurrentUser();

        LocalDateTime now = LocalDateTime.now();

        Fast_task fast_task = new Fast_task();
        fast_task.setCreate_time(now);
        fast_task.setOriginal_text(jsonObject.getString("original_text"));
        fast_task.setOriginal_language(jsonObject.getString("original_language"));
        fast_task.setTranslate_text(jsonObject.getString("translate_text"));
        fast_task.setTranslate_language(jsonObject.getString("translate_language"));
        fast_task.setName(creatNameByTime(now));
        fast_task.setUid(user.getId());
        iFast_taskService.save(fast_task);
        return RespBean.ok();
    }


    @GetMapping("/")
    public RespBean getFast_TaskList() {
        User user = SecurityUtils.getCurrentUser();
        List<Fast_task> fast_taskList = iFast_taskService.getLastFast_task(user.getId());
        return RespBean.ok(fast_taskList);
    }

    @GetMapping("/listByDate")
    public RespBean getFast_TaskListByDate(@RequestParam String name) {
        LOGGER.debug(name);
        User user = SecurityUtils.getCurrentUser();
        Map map = iFast_taskService.getFast_taskByDate(user.getId(), name);
        return RespBean.ok(map);
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody JSONObject jsonObject) {
        LOGGER.debug(jsonObject.toJSONString());

        String file_path = personalConfig.getMake_file_dir() + File.separator + UUID.randomUUID().toString() + ".txt";
        byte[] content = new byte[0];
        File tex_file = new File(file_path);
        if (!tex_file.getParentFile().exists()) {
            tex_file.getParentFile().mkdirs();
        }
        try {
            FileUtils.write(tex_file, jsonObject.getString("translate"), "utf-8");
            content = FileUtils.readFileToByteArray(tex_file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "translate.txt");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public RespBean getFast_TaskById(@PathVariable String id) {
        Long tid = Long.valueOf(id);
        Fast_task fast_task = iFast_taskService.getFast_TaskById(tid);
        return RespBean.ok(fast_task);
    }

    @PostMapping("/translate")
    public RespBean fast_translate(@RequestParam String text, @RequestParam String srcLang, @RequestParam String tgtLang) {
        String translate_text;
        Map<String, Object> result = null;

        if (srcLang.equals("auto")) {
            if (LanguageUtils.isVietnamString(text)) {
                srcLang = "vi";
            } else {
                srcLang = "en";
            }
        }


        if (srcLang.equals("en")) {
            result = translateService_xiaoniu.translate(text, srcLang, "zh");
        } else if (srcLang.equals("vi")) {
            result = translateService_pingsoft.translate(text, srcLang, "zh");
        }
        if ((Integer) result.get("code") == 0) {
            translate_text = ((HashMap<String, String>) result.get("data")).get("tgtText");
        } else {
            translate_text = (String) result.get("message");
        }

        Map<String, String> map = new HashMap<>();
        map.put("tr", translate_text);
        return RespBean.ok(map);
    }


    private String creatNameByTime(LocalDateTime localDateTime) {
        return Constant.FAST_TASK_NAME_PREFIX + dtf.format(localDateTime);
    }


}
