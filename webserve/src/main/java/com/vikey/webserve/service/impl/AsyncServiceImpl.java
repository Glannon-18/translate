package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.vikey.webserve.Constant;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.service.*;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
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

    @Resource
    private IFast_taskService fast_taskService;

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
                String result;
                result = fast_taskService.translate(content.getContent(), srcLang, tgtLang);
                String translate_file_name = UUID.randomUUID().toString() + "." + extend;
                String translate_file_path = personalConfig.getTranslate_dir() + File.separator + translate_file_name;
                File output = new File(translate_file_path);
                if (!output.getParentFile().exists()) {
                    output.getParentFile().mkdirs();
                }
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
}
