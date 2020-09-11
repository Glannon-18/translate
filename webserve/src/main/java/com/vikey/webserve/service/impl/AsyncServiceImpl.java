package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.vikey.webserve.Constant;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.service.Content;
import com.vikey.webserve.service.DocxContent;
import com.vikey.webserve.service.IAnnexeService;
import com.vikey.webserve.service.IAsyncService;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class AsyncServiceImpl implements IAsyncService {

    private static HttpClient HTTPCLIENT = HttpClientBuilder.create().setMaxConnTotal(20).setMaxConnPerRoute(10).build();

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncServiceImpl.class);

    private static final double LENGTH = 1400;

    @Resource
    private PersonalConfig personalConfig;

    @Resource
    private IAnnexeService iAnnexeService;

    @Override
    @Async("fileTranslateExecutor")
    public void translate(List<Annexe> annexes, String srcLang, String tgtLang) {
        for (Annexe annexe : annexes) {
            LOGGER.info(String.format("正在处理文件名为 %s 的翻译任务", annexe.getName()));
            Content content = null;
            String extend = annexe.getName().split("\\.")[1];
            if (extend.equals("txt")) {
                content = new TxtContent(new File(personalConfig.getUpload_dir() + File.separator + annexe.getPath()));
            } else if (extend.equals("docx")) {
                content = new DocxContent(new File(personalConfig.getUpload_dir() + File.separator + annexe.getPath()));
            }
            try {
                String result = batch_xiaoniu(content.getContent(), srcLang, tgtLang);
                String translate_file_name = UUID.randomUUID().toString() + "." + extend;
                String translate_file_path = personalConfig.getTranslate_dir() + File.separator + translate_file_name;
                File output = new File(translate_file_path);
                if (!output.getParentFile().exists()) {
                    output.getParentFile().mkdirs();
                }
//                JSONObject jsonObject = JSONObject.parseObject(result);
                content.write(result, output);
                UpdateWrapper<Annexe> annexeUpdateWrapper = new UpdateWrapper<>();
                annexeUpdateWrapper.set("translate_path", translate_file_name).set("status", Constant.ANNEXE_STATUS_PROCESSED).eq("id", annexe.getId());
                iAnnexeService.update(annexeUpdateWrapper);
            } catch (IOException | MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private String translate(String text, String src_lan, String tat_lan) throws IOException {
        HttpPost post = new HttpPost(personalConfig.getTranslate_api_url());
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("method", "translate"));
        urlParameters.add(new BasicNameValuePair("srcLang", src_lan));
        urlParameters.add(new BasicNameValuePair("tgtLang", tat_lan));
        urlParameters.add(new BasicNameValuePair("useSocket", "True"));
        urlParameters.add(new BasicNameValuePair("text", src_lan.equals("vi") ? "startnmtpy " + text : text));
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(15000).setConnectTimeout(3000).build();
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
        EntityUtils.consume(response.getEntity());
        return result.toString();
    }

    private String translate_xiaoniu(String text, String from, String to) throws Exception {
        HttpPost post = new HttpPost(personalConfig.getTranslate_api_url_xiaoniu());
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
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
//        tgt_text
        JSONObject result_obj = JSONObject.parseObject(result.toString());
        EntityUtils.consume(response.getEntity());
        if (result_obj.containsKey("tgt_text")) {
            return result_obj.getString("tgt_text");
        } else {
            throw new Exception("小牛接口翻译异常，错误代码" + result_obj.get("error_code"));
        }
    }


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
}
