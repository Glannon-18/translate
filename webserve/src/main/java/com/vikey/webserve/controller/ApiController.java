package com.vikey.webserve.controller;

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

    @Resource
    private PersonalConfig personalConfig;

    @Resource
    private PingSoft pingSoft;

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
        String srcLang = jsonArg.getString("srcLang");
        if (srcLang.equals("vi")) {
            translateService = translateService_pingsoft;
        } else if (srcLang.equals("en")) {
            translateService = translateService_xiaoniu;
        }
        String tgtLang = jsonArg.getString("tgtLang");
        String srcText = jsonArg.getString("srcText");
        Map<String, Object> result = translateService.translate(srcText, srcLang, tgtLang);
        return result;
    }


    //    http://localhost:80/d:/新建文本文档(2).txt
    @PostMapping("/DocUpload")
    public Map<String, Object> docUpload(@RequestBody JSONObject jsonObject) throws Exception {


        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");
        String srcLang = jsonArg.getString("srcLang");
        String tgtLang = jsonArg.getString("tgtLang");
        String srcFileUrl = jsonArg.getString("srcFileUrl");
        String srcFileFormat = jsonArg.getString("srcFileFormat");


        String taskId = UUID.randomUUID().toString() + "_" + srcLang + "-" + tgtLang;
        String dir = personalConfig.getUpload_dir() + File.separatorChar + taskId;
        String fileName = srcFileUrl.substring(srcFileUrl.lastIndexOf("/") + 1);
        String uploadPath = dir + File.separatorChar + fileName;

        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(srcFileUrl);
            HttpResponse response = client.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            File file = new File(uploadPath);
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
        }

        Content content = null;
        String translateFileName = fileName.split("\\.")[0] + "_translate." + fileName.split("\\.")[1];
        String translateFilePath = dir + File.separatorChar + translateFileName;

        if (srcFileFormat.equals("txt")) {
            content = new TxtContent(new File(uploadPath));
        } else if (srcFileFormat.equals("docx")) {
            content = new DocxContent(new File(uploadPath));
        }

        String translateText = null;

        if ((srcLang.equals("vi"))) {
            Map<String, Object> translate = translate(content.getContent(), srcLang, tgtLang);
            translateText = ((Map<String, String>) translate.get
                    ("data")).get("tgtText");
            content.write(translateText, new File(translateFilePath));
        } else if (srcLang.equals("en")) {
            translateText = batch_xiaoniu(content.getContent(), srcLang, tgtLang);
        }

        content.write(translateText, new File(translateFilePath));

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", "0");
        map.put("message", "");

        HashMap<String, String> data = new HashMap<>();
        data.put("taskId", taskId);
        map.put("data", data);

        return map;
    }


    @PostMapping("/DocResult")
    public Map<String, Object> docResult(@RequestBody JSONObject jsonObject) throws FileNotFoundException {

        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");

        String taskId = jsonArg.getString("taskId");

        HashMap<String, Object> map = new HashMap<>();

        String dir = personalConfig.getUpload_dir() + File.separatorChar + taskId;
        File file = new File(dir);

        //文件夹不存在
        if (!file.exists()) {
            map.put("code", "500");
            map.put("message", "not found");
            return map;
        }
        String srcLang = taskId.split("_")[1].split("-")[0];
        String tgtLang = taskId.split("_")[1].split("-")[1];

        map.put("code", "0");
        map.put("message", "");

        HashMap<String, String> data = new HashMap<>();
        data.put("srcLang", srcLang);
        data.put("tgtLang", tgtLang);

        if (file.list().length != 2) {
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
        String filePath = personalConfig.getUpload_dir() + File.separatorChar + taskId;
        File dir = new File(filePath);
        String translateFileName = null;
        for (String s : dir.list()) {
            if (s.indexOf("translate") != -1) {
                translateFileName = s;
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(translateFileName, "utf-8"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(new File(filePath + File.separatorChar + translateFileName)), headers, HttpStatus.CREATED);
    }


    private Map<String, Object> translate(String text, String srcLang, String tgtLang) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        if (srcLang.equals("en")) {
            HttpPost post = new HttpPost(personalConfig.getTranslate_api_url_xiaoniu());
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("from", srcLang));
            urlParameters.add(new BasicNameValuePair("to", tgtLang));
            urlParameters.add(new BasicNameValuePair("src_text", text));
            urlParameters.add(new BasicNameValuePair("apikey", personalConfig.getApiKey_xiaoniu()));
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).build();
            post.setConfig(requestConfig);
            post.setEntity(new UrlEncodedFormEntity(urlParameters, "utf-8"));

            HttpResponse response = HTTPCLIENT.execute(post);
            StringBuffer result = new StringBuffer();
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "utf-8"));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            JSONObject result_obj = JSONObject.parseObject(result.toString());
            EntityUtils.consume(response.getEntity());
            if (result_obj.containsKey("tgt_text")) {
                map.put("code", "0");
                map.put("message", "");
                HashMap<String, String> result_ = new HashMap<>();
                result_.put("srcLang", srcLang);
                result_.put("tgtLang", tgtLang);
                result_.put("tgtText", result_obj.getString("tgt_text"));
                map.put("data", result_);
            } else {
//                throw new Exception("小牛接口翻译异常，错误代码" + result_obj.get("error_code"));
                map.put("code", "500");
                map.put("message", "error");
            }
            return map;

        } else if ((srcLang.equals("vi"))) {
            String result = pingSoft.translate(text, srcLang, tgtLang);
            map.put("code", "0");
            map.put("message", "");

            HashMap<String, String> result_ = new HashMap<>();
            result_.put("srcLang", srcLang);
            result_.put("tgtLang", tgtLang);
            result_.put("tgtText", result);
//            return tmp = (String) jo.get("tgt");
            map.put("data", result_);

            return map;
        }


        return null;
    }

    private static final double LENGTH = 1400;


    /**
     * 小牛长文本文件翻译
     *
     * @param text
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    private String batch_xiaoniu(String text, String from, String to) throws Exception {
        double batchNum = Math.ceil(new Double(text.length()) / LENGTH);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < batchNum; i++) {
            int end = (i + 1) * LENGTH > text.length() ? text.length() : (int) ((i + 1) * LENGTH);
            String batch_text = text.substring((int) (i * LENGTH), end);
            try {
                String batch_translate = translate_xiaoniu(batch_text, from, to);
                Thread.sleep(5100);
                result.append(batch_translate);
            } catch (Exception e) {
                throw e;
            }
        }
        return result.toString();
    }

    private String translate_xiaoniu(String text, String from, String to) throws Exception {
        HttpPost post = new HttpPost(personalConfig.getTranslate_api_url_xiaoniu());
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("from", from));
        urlParameters.add(new BasicNameValuePair("to", to));
        urlParameters.add(new BasicNameValuePair("src_text", text));
        urlParameters.add(new BasicNameValuePair("apikey", personalConfig.getApiKey_xiaoniu()));
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).build();
        post.setConfig(requestConfig);
        post.setEntity(new UrlEncodedFormEntity(urlParameters, "utf-8"));

        HttpResponse response = HTTPCLIENT.execute(post);
        StringBuffer result = new StringBuffer();
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), "utf-8"));
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        JSONObject result_obj = JSONObject.parseObject(result.toString());
        EntityUtils.consume(response.getEntity());
        if (result_obj.containsKey("tgt_text")) {
            return result_obj.getString("tgt_text");
        } else {
            throw new Exception("小牛接口翻译异常，错误代码" + result_obj.get("error_code"));
        }
    }


    //    http://localhost:80/d:/新建文本文档(2).txt
    @PostMapping("/DocUpload2")
    public Map<String, Object> docUpload2(@RequestBody JSONObject jsonObject) throws Exception {
        HashMap<String, Object> map = new HashMap<>();


        JSONObject jsonArg = jsonObject.getJSONObject("jsonArg");
        String srcLang = jsonArg.getString("srcLang");
        String tgtLang = jsonArg.getString("tgtLang");
        String srcFileUrl = jsonArg.getString("srcFileUrl");
        String srcFileFormat = jsonArg.getString("srcFileFormat");

        String dir = personalConfig.getUpload_dir();
        String fileName = srcFileUrl.substring(srcFileUrl.lastIndexOf("/") + 1);
        String uploadPath = dir + File.separatorChar + fileName;

        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(srcFileUrl);
            HttpResponse response = client.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            File file = new File(uploadPath);
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
        Long task_id = createAnnTask(srcLang, tgtLang, uploadPath);

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
    public Long createAnnTask(String srcLang, String tgtLang, String path) {

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
        String fileName = path.substring(path.lastIndexOf(File.separatorChar) + 1);
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);
        annexe.setName(fileName);
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

    @PostMapping("/DocResult2")
    public Map<String, Object> docResult2(@RequestBody JSONObject jsonObject) throws IOException {

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
            data.put("tgtFileUrl", personalConfig.getLocal_ip() + "/mte/service/download2/" + taskId);
            map.put("data", data);
            return map;
        }
    }

    @GetMapping("/download2/{taskId}")
    public ResponseEntity<?> export2(@PathVariable("taskId") String taskId) throws IOException {

        QueryWrapper<Atask_ann> annexeQueryWrapper = new QueryWrapper<>();
        annexeQueryWrapper.eq("atid", taskId);
        Atask_ann one = atask_annService.getOne(annexeQueryWrapper);
        Annexe annexe = annexeService.getById(one.getAid());
        String filePath = personalConfig.getUpload_dir() + File.separatorChar + annexe.getTranslate_path();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(annexe.getName(), "utf-8"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(new File(filePath)), headers, HttpStatus.CREATED);
    }

}
