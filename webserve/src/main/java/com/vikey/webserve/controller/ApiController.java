package com.vikey.webserve.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.Constant;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.entity.Annexe_task;
import com.vikey.webserve.entity.Atask_ann;
import com.vikey.webserve.service.*;
import com.vikey.webserve.utils.LanguageUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/mte/service")
public class ApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    @Resource
    private PersonalConfig personalConfig;


    @Resource(name = "xiaoNiuTranslateService")
    private TranslateService translateService_xiaoniu;

    @Resource(name = "pingSoftTranslateService")
    private TranslateService translateService_pingsoft;

    @Resource
    private IAnnexe_taskService annexe_taskService;

    @Resource
    private IAnnexeService annexeService;

    @Resource
    private IAtask_annService atask_annService;

    @Resource
    private IAsyncService iAsyncService;


    @PostMapping("/Text")
    public Map text(@RequestBody JSONObject jsonObject) {
        LOGGER.info("接收参数：" + jsonObject.toString());
        TranslateService translateService = null;
        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");
        String srcText = jsonArg.getString("srcText");//zh-cn
        String srcLang = jsonArg.getString("srcLang");
        if (srcLang.equals("auto")) {
            if (LanguageUtils.isVietnamString(srcText)) {
                translateService = translateService_pingsoft;
                srcLang = "vi";
            } else {
                translateService = translateService_xiaoniu;
                srcLang = "en";
            }
        } else if (srcLang.equals("vi")) {
            translateService = translateService_pingsoft;
        } else if (srcLang.equals("en")) {
            translateService = translateService_xiaoniu;
        }

        Map<String, Object> result = translateService.translate(srcText, srcLang, "zh");
        return result;
    }


    @PostMapping("/DocUpload")
    public Map<String, Object> docUpload(@RequestBody JSONObject jsonObject) {
        LOGGER.info("接收参数：" + jsonObject.toString());
        HashMap<String, Object> map = new HashMap<>();

        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");
        String srcLang = jsonArg.getString("srcLang");
        String tgtLang = jsonArg.getString("tgtLang");
        String srcFileUrl = jsonArg.getString("srcFileUrl");
//        String srcFileFormat = jsonArg.getString("srcFileFormat");

        String dir = personalConfig.getUpload_dir();
        String fileName = srcFileUrl.substring(srcFileUrl.lastIndexOf("/") + 1);
        String type = fileName.substring(fileName.indexOf(".") + 1);
        String uploadPath = UUID.randomUUID().toString() + "." + type;

        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(srcFileUrl);
            HttpResponse response = client.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            File file = new File(dir + File.separatorChar + uploadPath);
            file.getParentFile().mkdirs();
            FileOutputStream fileout = new FileOutputStream(file);
            /**
             * 根据实际运行效果 设置缓冲区大小
             */
            byte[] buffer = new byte[1024];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer, 0, ch);
            }
            is.close();
            fileout.flush();
            fileout.close();

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", "500");
            map.put("message", "IO错误");
            return map;

        }
        Long task_id = createAnnTask(srcLang, tgtLang, fileName, type, uploadPath);

        QueryWrapper<Atask_ann> annexeQueryWrapper = new QueryWrapper<>();
        annexeQueryWrapper.eq("atid", task_id);
        Atask_ann one = atask_annService.getOne(annexeQueryWrapper);

        Annexe annexe = annexeService.getById(one.getAid());
        ArrayList<Annexe> annexes = new ArrayList<>();
        annexes.add(annexe);
        iAsyncService.translate(annexes, srcLang, tgtLang);


        map.put("code", "0");
        map.put("message", "");

        HashMap<String, String> data = new HashMap<>();
        data.put("taskId", task_id.toString());
        map.put("data", data);

        return map;
    }

    /**
     * @param srcLang
     * @param tgtLang
     * @param path    上传文件保存路径
     */
    @Transactional
    public Long createAnnTask(String srcLang, String tgtLang, String name, String type, String path) {

        Annexe_task annexe_task = new Annexe_task();
        annexe_task.setCreate_time(LocalDateTime.now());
        annexe_task.setDiscard(Constant.NOT_DELETE);
        annexe_task.setName("接口上传");
        annexe_task.setOriginal_language(srcLang);
        annexe_task.setTranslate_language(tgtLang);
        //接口创建用户外键为5
        annexe_task.setUid(5l);
        annexe_taskService.save(annexe_task);
        Long annexe_taskId = annexe_task.getId();


        Annexe annexe = new Annexe();
        annexe.setCreate_time(LocalDateTime.now());
        annexe.setName(name);
        annexe.setPath(path);
        annexe.setType(type);
        annexe.setDiscard(Constant.NOT_DELETE);
        annexe.setOriginal_language(srcLang);
        annexe.setStatus(Constant.ANNEXE_STATUS_UNPROCESSED);
        annexeService.save(annexe);
        Long annexeId = annexe.getId();

        Atask_ann atask_ann = new Atask_ann();
        atask_ann.setAtid(annexe_taskId);
        atask_ann.setAid(annexeId);
        atask_annService.save(atask_ann);


        return annexe_taskId;


    }

    @PostMapping("/DocResult")
    public Map<String, Object> docResult(@RequestBody JSONObject jsonObject) {
        LOGGER.info("接收参数：" + jsonObject.toString());

        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");

        String taskId = jsonArg.getString("taskId");

        HashMap<String, Object> map = new HashMap<>();


        QueryWrapper<Atask_ann> annexeQueryWrapper = new QueryWrapper<>();
        annexeQueryWrapper.eq("atid", taskId);
        Atask_ann one = atask_annService.getOne(annexeQueryWrapper);
        Annexe_task annexe_task = annexe_taskService.getById(taskId);

        Annexe annexe = annexeService.getById(one.getAid());

        HashMap<String, String> data = new HashMap<>();
        data.put("srcLang", annexe_task.getOriginal_language());
        data.put("tgtLang", annexe_task.getTranslate_language());

        if (annexe.getStatus().equals(Constant.ANNEXE_STATUS_UNPROCESSED)) {
            data.put("process", "85");
            map.put("data", data);
            return map;
        } else {
            data.put("process", "100");
            data.put("tgtFileUrl", personalConfig.getLocal_ip() + "/mte/service/download/" + taskId);
            map.put("data", data);
            return map;
        }
    }

    @GetMapping("/download/{taskId}")
    public ResponseEntity<?> export(@PathVariable("taskId") String taskId) throws IOException {

        LOGGER.info("接收参数taskId：" + taskId);

        QueryWrapper<Atask_ann> annexeQueryWrapper = new QueryWrapper<>();
        annexeQueryWrapper.eq("atid", taskId);
        Atask_ann one = atask_annService.getOne(annexeQueryWrapper);
        Annexe annexe = annexeService.getById(one.getAid());
        String filePath = personalConfig.getTranslate_dir() + File.separatorChar + annexe.getTranslate_path();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(annexe.getName(), "utf-8"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(new File(filePath)), headers, HttpStatus.CREATED);
    }


    @PostMapping("/BatchDocUpload")
    public Map<String, Object> batchDocUpload(@RequestBody JSONObject jsonObject) {
        LOGGER.info("接收参数：" + jsonObject.toString());

        HashMap<String, Object> result = new HashMap<>();

        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");
        String srcLang = jsonArg.getString("srcLang");
        String tgtLang = jsonArg.getString("tgtLang");
        JSONArray srcFileInfo = jsonArg.getJSONArray("srcFileInfo");

        ArrayList<Map<String, String>> maps = new ArrayList<>();

        for (int i = 0; i < srcFileInfo.size(); i++) {
            JSONObject info = srcFileInfo.getJSONObject(i);
            String srcFileUrl = info.getString("srcFileUrl");
            String dir = personalConfig.getUpload_dir();
            String fileName = srcFileUrl.substring(srcFileUrl.lastIndexOf("/") + 1);
            String type = fileName.substring(fileName.indexOf(".") + 1);
            String uploadPath = UUID.randomUUID().toString() + "." + type;


            try {
                HttpClient client = HttpClients.createDefault();
                HttpGet httpget = new HttpGet(srcFileUrl);
                HttpResponse response = client.execute(httpget);

                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                File file = new File(dir + File.separatorChar + uploadPath);
                file.getParentFile().mkdirs();
                FileOutputStream fileout = new FileOutputStream(file);
                /**
                 * 根据实际运行效果 设置缓冲区大小
                 */
                byte[] buffer = new byte[1024];
                int ch = 0;
                while ((ch = is.read(buffer)) != -1) {
                    fileout.write(buffer, 0, ch);
                }
                is.close();
                fileout.flush();
                fileout.close();


                HashMap<String, String> map = new HashMap<>();
                map.put("fileName", fileName);
                map.put("type", type);
                map.put("uploadPath", uploadPath);

                maps.add(map);


            } catch (Exception e) {
                e.printStackTrace();
                result.put("code", "500");
                result.put("message", e.getMessage());
                return result;
            }

        }

        Long task_id = createAnnTask(srcLang, tgtLang, maps);

        QueryWrapper<Atask_ann> annexeQueryWrapper = new QueryWrapper<>();
        annexeQueryWrapper.eq("atid", task_id);
        List<Atask_ann> atask_anns = atask_annService.getBaseMapper().selectList(annexeQueryWrapper);

        for (Atask_ann atask_ann : atask_anns) {
            Annexe annexe = annexeService.getById(atask_ann.getAid());
            ArrayList<Annexe> annexes = new ArrayList<>();
            annexes.add(annexe);
            iAsyncService.translate(annexes, srcLang, tgtLang);
        }

        result.put("code", "0");
        result.put("message", "");

        HashMap<String, String> data = new HashMap<>();
        data.put("taskId", task_id.toString());
        result.put("data", data);

        return result;
    }

    @Transactional
    public Long createAnnTask(String srcLang, String tgtLang, List<Map<String, String>> annexe_info) {

        Annexe_task annexe_task = new Annexe_task();
        annexe_task.setCreate_time(LocalDateTime.now());
        annexe_task.setDiscard(Constant.NOT_DELETE);
        annexe_task.setName("接口上传");
        annexe_task.setOriginal_language(srcLang);
        annexe_task.setTranslate_language(tgtLang);
        //接口创建用户外键为5
        annexe_task.setUid(5l);
        annexe_taskService.save(annexe_task);
        Long annexe_taskId = annexe_task.getId();

        for (Map<String, String> map : annexe_info) {
            String name = map.get("fileName");
            String type = map.get("type");
            String path = map.get("uploadPath");


            Annexe annexe = new Annexe();
            annexe.setCreate_time(LocalDateTime.now());
            annexe.setName(name);
            annexe.setPath(path);
            annexe.setType(type);
            annexe.setDiscard(Constant.NOT_DELETE);
            annexe.setOriginal_language(srcLang);
            annexe.setStatus(Constant.ANNEXE_STATUS_UNPROCESSED);
            annexeService.save(annexe);
            Long annexeId = annexe.getId();

            Atask_ann atask_ann = new Atask_ann();
            atask_ann.setAtid(annexe_taskId);
            atask_ann.setAid(annexeId);
            atask_annService.save(atask_ann);


        }


        return annexe_taskId;

    }

    @RequestMapping("/BatchDocResult")
    public Map<String, Object> batchDocResult(@RequestBody JSONObject jsonObject) {
        LOGGER.info("接收参数：" + jsonObject.toString());
        HashMap<String, Object> result = new HashMap<>();

        HashMap<String, Object> data = new HashMap<>();

        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");
        String taskId = jsonArg.getString("taskId");
        Annexe_task annexe_task = annexe_taskService.getById(new Long(taskId));
        String srcLang = annexe_task.getOriginal_language();
        String tgtLang = annexe_task.getTranslate_language();

        data.put("srcLang", srcLang);
        data.put("tgtLang", tgtLang);

        QueryWrapper<Atask_ann> atask_annQueryWrapper = new QueryWrapper<>();
        atask_annQueryWrapper.eq("atid", new Long(taskId));
        List<Atask_ann> anns = atask_annService.list(atask_annQueryWrapper);
        ArrayList<Map<String, String>> transResult = new ArrayList<>();

        for (Atask_ann ann : anns) {
            HashMap<String, String> map = new HashMap<>();
            Annexe annexe = annexeService.getBaseMapper().selectById(ann.getAid());
            map.put("process", annexe.getStatus().equals(Constant.ANNEXE_STATUS_PROCESSED) ? "100" : "85");
            map.put("srcFileUrl", personalConfig.getLocal_ip() + "/mte/service/download/" + annexe.getId() + "/src");
            map.put("tgtFileUrl", personalConfig.getLocal_ip() + "/mte/service/download/" + annexe.getId() + "/tgt");
            transResult.add(map);

        }

        data.put("transResult", transResult);

        result.put("code", "0");
        result.put("message", "");
        result.put("dara", data);
        return result;
    }


    @GetMapping("/download/{annexeId}/{type}")
    public ResponseEntity<?> export(@PathVariable("annexeId") String annexeId, @PathVariable String type) throws IOException {
        LOGGER.info(String.format("接收参数：annexeId=%s,type=%s", annexeId, type));
        Annexe annexe = annexeService.getById(new Long(annexeId));
        String filePath = null;
        if (type.equals("src")) {
            filePath = personalConfig.getUpload_dir() + File.separatorChar + annexe.getPath();
        } else if (type.equals("tgt")) {
            filePath = personalConfig.getTranslate_dir() + File.separatorChar + annexe.getTranslate_path();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(annexe.getName(), "utf-8"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(new File(filePath)), headers, HttpStatus.CREATED);
    }
}
