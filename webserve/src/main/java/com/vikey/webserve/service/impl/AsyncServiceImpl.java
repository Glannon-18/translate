package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.vikey.webserve.Constant;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.service.Content;
import com.vikey.webserve.service.IAnnexeService;
import com.vikey.webserve.service.IAsyncService;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AsyncServiceImpl implements IAsyncService {

    private static HttpClient HTTPCLIENT = HttpClientBuilder.create().setMaxConnTotal(10).setMaxConnPerRoute(10).build();

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Resource
    private PersonalConfig personalConfig;

    @Resource
    private IAnnexeService iAnnexeService;

    @Override
    @Async("fileTranslateExecutor")
    public void translate(Annexe annexe, String srcLang, String tgtLang) {
        LOGGER.info(String.format("正在处理文件名为 %s 的翻译任务", annexe.getName()));
        Content content = null;
        String extend = annexe.getName().split("\\.")[1];
        if (extend.equals("txt")) {
            content = new TxtContent(new File(personalConfig.getUpload_dir() + File.separator + annexe.getPath()));
        }
        try {
            HttpPost post = new HttpPost(personalConfig.getTranslate_api_url());
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("method", "translate"));
            urlParameters.add(new BasicNameValuePair("srcLang", srcLang));
            urlParameters.add(new BasicNameValuePair("tgtLang", tgtLang));
            urlParameters.add(new BasicNameValuePair("useSocket", "True"));
            urlParameters.add(new BasicNameValuePair("text", srcLang.equals("vi") ? "startnmtpy " + content.getContent() : content.getContent()));
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
            String translate_file_name = UUID.randomUUID().toString() + "." + extend;
            String translate_file_path = personalConfig.getTranslate_dir() + File.separator + translate_file_name;
            File output = new File(translate_file_path);
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }
            JSONObject jsonObject = JSONObject.parseObject(result.toString());
            content.write(jsonObject.getString("data"), output);
            UpdateWrapper<Annexe> annexeUpdateWrapper = new UpdateWrapper<>();
            annexeUpdateWrapper.set("translate_path", translate_file_name).set("status", Constant.ANNEXE_STATUS_PROCESSED).eq("id", annexe.getId());
            iAnnexeService.update(annexeUpdateWrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
