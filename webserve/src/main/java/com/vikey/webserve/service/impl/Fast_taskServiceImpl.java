package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Fast_task;
import com.vikey.webserve.mapper.Fast_taskMapper;
import com.vikey.webserve.service.IFast_taskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@Service
public class Fast_taskServiceImpl extends ServiceImpl<Fast_taskMapper, Fast_task> implements IFast_taskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Fast_taskServiceImpl.class);

    private HttpClient httpClient = HttpClientBuilder.create().build();

    @Resource
    private PersonalConfig personalConfig;


    @Override
    public List<Fast_task> getLastFast_task(Long uid) {
        List<Fast_task> fast_taskList;
        fast_taskList = getBaseMapper().selectLastFast_task(uid);
        return fast_taskList;
    }

    @Override
    public LinkedHashMap<String, List<Fast_task>> getFast_taskByDate(Long uid, String name) {
        LinkedHashMap<String, List<Fast_task>> map = new LinkedHashMap<>();
        List<Fast_task> fast_taskList;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        fast_taskList = getBaseMapper().selectFast_taskByDate(uid, name);
        fast_taskList.stream().forEach(t -> {
            String date = dateTimeFormatter.format(t.getCreate_time());
            if (map.containsKey(date)) {
                map.get(date).add(t);
            } else {
                List<Fast_task> list = new ArrayList<>();
                list.add(t);
                map.put(date, list);
            }
        });
        return map;
    }

    @Override
    public Fast_task getFast_TaskById(Long id) {
        return getById(id);
    }

    @Override
    public String translate(String text, String srcLang, String tgtLang) throws Exception {
        HttpPost post = new HttpPost(personalConfig.getTranslate_api_url());
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("method", "translate"));
        urlParameters.add(new BasicNameValuePair("srcLang", srcLang));
        urlParameters.add(new BasicNameValuePair("tgtLang", tgtLang));
        urlParameters.add(new BasicNameValuePair("useSocket", "True"));
        urlParameters.add(new BasicNameValuePair("text", srcLang.equals("vi") ? "startnmtpy " + text : text));

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).build();
        post.setConfig(requestConfig);
        post.setEntity(new UrlEncodedFormEntity(urlParameters, "utf-8"));
        HttpResponse response = httpClient.execute(post);
        LOGGER.info("翻译接口返回码： " + response.getStatusLine().getStatusCode());
        StringBuffer result = new StringBuffer();
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), "utf-8"));
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        JSONObject result_obj = JSONObject.parseObject(result.toString());
        if (result_obj.getString("success").equals("true")) {
            return result_obj.getString("data").replaceAll("\\$number", "");
        } else {
            throw new Exception("翻译接口返回的success为false");
        }
    }
}
