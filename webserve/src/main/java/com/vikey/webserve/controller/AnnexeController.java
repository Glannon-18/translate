package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vikey.webserve.Constant;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.RespPageBean;
import com.vikey.webserve.service.IAnnexeService;
import com.vikey.webserve.utils.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/annexe")
public class AnnexeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnexeController.class);

    @Resource
    private PersonalConfig PersonalConfig;


    @Resource
    private IAnnexeService iAnnexeService;

    @Resource
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/page")
    public RespPageBean getAnnexePage(@RequestParam String currentPage, @RequestParam String atid) {
        IPage<Annexe> page = iAnnexeService.getAnnexeByPage(Integer.valueOf(currentPage), Constant.PAGESIZE, Long.valueOf(atid));
        return new RespPageBean(page.getTotal(), page.getRecords(), page.getSize());
    }

    @DeleteMapping("/")
    public RespBean deleteAnnexe(@RequestParam String ids) {
        UpdateWrapper<Annexe> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("discard", Constant.DELETE).in("id", convert(ids));
        iAnnexeService.getBaseMapper().update(null, updateWrapper);
        return RespBean.ok("删除文件成功！");
    }


    @PostMapping("/export")
    public ResponseEntity<?> export(@RequestBody String json) throws IOException {

        JSONObject jsonObject = JSONObject.parseObject(json);
        List<Long> list = convert(jsonObject.getString("ids"));
        QueryWrapper<Annexe> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "name", "path", "translate_path").in("id", list);
        List<Annexe> annexeList = iAnnexeService.getBaseMapper().selectList(queryWrapper);
        try {
            String zip_path = PersonalConfig.getMake_file_dir() + File.separator + "download.zip";
            File file = new File(zip_path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ZipUtils.toZip(annexeList, fileOutputStream, PersonalConfig.getTranslate_dir());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "download.zip");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(new File(zip_path)), headers, HttpStatus.CREATED);
        } catch (FileNotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> map = new HashMap<>();
            map.put("msg", e.getMessage());
            return new ResponseEntity<Map>(map, headers, HttpStatus.OK);
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> map = new HashMap<>();
            map.put("msg", e.getMessage());
            return new ResponseEntity<Map>(map, headers, HttpStatus.OK);
        }
    }

    @GetMapping("/annexeStatus")
    public RespBean annexeStatus() {
        LocalDateTime time = LocalDateTime.now();
        //24小时内文档接入量
        Integer Au24h = iAnnexeService.getAnnexeCount(time.minusHours(24l), null);
        //30天内文档接入量
        Integer Au30d = iAnnexeService.getAnnexeCount(time.minusDays(30l), null);
        //24小时内文档接处理量
        Integer Ap24h = iAnnexeService.getAnnexeCount(time.minusHours(24l), Constant.ANNEXE_STATUS_PROCESSED);
        //30天内文档接入量
        Integer Ap30d = iAnnexeService.getAnnexeCount(time.minusDays(30l), Constant.ANNEXE_STATUS_PROCESSED);
        //所有时间文档接入量
        Integer AuAll = iAnnexeService.getAnnexeCount(null, Constant.ANNEXE_STATUS_UNPROCESSED);
        //所有时间文档处理量
        Integer ApAll = iAnnexeService.getAnnexeCount(null, Constant.ANNEXE_STATUS_PROCESSED);
        Map<String, Integer> map = new HashMap<String, Integer>() {{
            put("Au24h", Au24h);
            put("Au30d", Au30d);
            put("Ap24h", Ap24h);
            put("Ap30d", Ap30d);
            put("AuAll", AuAll);
            put("ApAll", ApAll);
        }};
        return RespBean.ok(map);
    }


    @GetMapping("/annexeCountByPeriod")
    public RespBean annexeCountByPeriod(@RequestParam String type) {

        String format = null;
        String x_format = null;
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> localDateTimes = new ArrayList<>();

        if ("24h".equals(type)) {
            format = "%Y-%m-%d %H:00:00";
            x_format = "HH时";
            LocalDateTime now_hour = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
            for (int i = 23; i >= 0; i--) {
                localDateTimes.add(now_hour.minusHours(i));
            }
        } else if ("30d".equals(type)) {
            format = "%Y-%m-%d 00:00:00";
            x_format = "dd日";
            LocalDateTime now_day = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
            for (int i = 29; i >= 0; i--) {
                localDateTimes.add(now_day.minusDays(i));
            }
        } else if ("all".equals(type)) {
            format = "%Y-%m";
            x_format = "yyyy年MM月";
            LocalDateTime minDateTime = minDateTime();
            LocalDateTime now_day = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
            do {
                localDateTimes.add(now_day);
                now_day = now_day.minusMonths(1);
            } while (now_day.compareTo(minDateTime) >= 0);

        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(x_format);
        localDateTimes = localDateTimes.stream().sorted().collect(Collectors.toList());

        List<String> x_string = localDateTimes.stream().map(t ->
                dateTimeFormatter.format(t)
        ).collect(Collectors.toList());


        List<Long> txt = iAnnexeService.getAnnexeCountByPeriod(localDateTimes, "txt", format).stream().map(map -> ((BigDecimal) map.get("count")).longValue()).collect(Collectors.toList());
        List<Long> word = iAnnexeService.getAnnexeCountByPeriod(localDateTimes, "docx", format).stream().map(map -> ((BigDecimal) map.get("count")).longValue()).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("txt", txt);
        result.put("word", word);
        result.put("x_string", x_string);
        return RespBean.ok(result);
    }

    @GetMapping("/getAnnexeCountByType")
    public RespBean getAnnexeCountByType(@RequestParam String type) {
        LocalDateTime after = null;

        if ("24h".equals(type)) {
            after = LocalDateTime.now().minusHours(24l);
        } else if ("30d".equals(type)) {
            after = LocalDateTime.now().minusDays(30l);
        } else if ("all".equals(type)) {
            after = null;
        }

        List<Map> right = iAnnexeService.getAnnexeCountByType(after);
        Map<String, Object> result = new HashMap<>();
        result.put("r", right);

        return RespBean.ok(result);

    }

    @GetMapping("/{id}")
    public RespBean getAnnexe(@PathVariable Long id) throws IOException {
        Annexe annexe = iAnnexeService.getAnnexeById(id);
        return RespBean.ok(annexe);
    }


    @GetMapping("/testRabbitmq")
    public RespBean testRabbitmq() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "test message^hello!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend(Constant.EXCHANGE_NAME, Constant.KEY, map, new CorrelationData("45656"));
        LOGGER.info("已经发送消息：" + map.toString());
        return RespBean.ok();
    }


    private List<Long> convert(String content) {
        List<Long> list = new ArrayList<>();
        Arrays.stream(content.split(",")).forEach(i ->
        {
            list.add(Long.valueOf(i));
        });
        return list;
    }

    private LocalDateTime minDateTime() {
        return iAnnexeService.minDateTime();
    }

}