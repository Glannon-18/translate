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
import com.vikey.webserve.service.impl.PingSoft;
import com.vikey.webserve.service.impl.TxtContent;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/mte/service")
public class ApiController {

    private static HttpClient HTTPCLIENT = HttpClientBuilder.create().setMaxConnTotal(20).setMaxConnPerRoute(10).build();

    private static final double LENGTH = 1400;
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
        TranslateService translateService = null;
        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");
        String srcText = jsonArg.getString("srcText");
        String srcLang = jsonArg.getString("srcLang");
        if (srcLang.equals("auto")) {
            if (isVietnamString(srcText)) {
                translateService = translateService_pingsoft;
            } else {
                translateService = translateService_xiaoniu;
            }
        } else if (srcLang.equals("vi")) {
            translateService = translateService_pingsoft;
        } else if (srcLang.equals("en")) {
            translateService = translateService_xiaoniu;
        }
        String tgtLang = jsonArg.getString("tgtLang");

        Map<String, Object> result = translateService.translate(srcText, srcLang, tgtLang);
        return result;
    }

    public boolean isVietnamString(String str) {
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (isVietnamChar(arr[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean isVietnamChar(char ch) {
        if ((ch >= 0x00C0 && ch <= 0x00C3) ||
                (ch >= 0x00C8 && ch <= 0x00CA) ||
                (ch >= 0x00CC && ch <= 0x00CD) ||
                (ch >= 0x00D2 && ch <= 0x00D5) ||
                (ch >= 0x00D9 && ch <= 0x00DA) ||
                (ch >= 0x00DD && ch <= 0x00DD) ||
                (ch >= 0x00E0 && ch <= 0x00E3) ||
                (ch >= 0x00E8 && ch <= 0x00EA) ||
                (ch >= 0x00EC && ch <= 0x00ED) ||
                (ch >= 0x00F2 && ch <= 0x00F5) ||
                (ch >= 0x00F9 && ch <= 0x00FA) ||
                (ch >= 0x00FD && ch <= 0x00FD) ||
                (ch >= 0x0102 && ch <= 0x0103) ||
                (ch >= 0x0110 && ch <= 0x0111) ||
                (ch >= 0x0128 && ch <= 0x0129) ||
                (ch >= 0x0168 && ch <= 0x0169) ||
                (ch >= 0x01A0 && ch <= 0x01A1) ||
                (ch >= 0x01AF && ch <= 0x01B0) ||
                (ch >= 0x1EA0 && ch <= 0x1EF9))
            return true;

        return false;
    }


    @PostMapping("/DocUpload")
    public Map<String, Object> docUpload(@RequestBody JSONObject jsonObject) {
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
    public Map<String, Object> docResult(@RequestBody JSONObject jsonObject) throws IOException {

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


            }

        }

        Long task_id = createAnnTask(srcLang, tgtLang, maps);

        //todo 还需调用异步文件翻译



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

}
