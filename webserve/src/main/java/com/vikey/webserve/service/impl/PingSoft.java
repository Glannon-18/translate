package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pingsoft.segmentation.ISentenceSegmenter;
import com.pingsoft.segmentation.Rule;
import com.pingsoft.segmentation.RuleSentenceSegmenter;
import com.pingsoft.translate.utils.LanguageInfoUtils;
import com.vikey.webserve.config.PersonalConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class PingSoft {


    private static HttpClient HTTPCLIENT = HttpClientBuilder.create().setMaxConnTotal(20).setMaxConnPerRoute(10).build();

    @Resource
    private PersonalConfig personalConfig;

    public String translate(String text, String src, String tgt) throws IOException {

        String[] strings = text.split("\n");

        String last_result = "";
        for (String string : strings) {
            string = string.trim().replaceAll("\"", "\\\"");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", 300);
            jsonObject.put("src", segmentation(string));
            JSONArray array = new JSONArray();
            array.add(jsonObject);
            String language = src + "_" + tgt;
            HttpPost post = new HttpPost(personalConfig.getTranslate_api_url().get(language));
            post.setHeader("Content-type", "application/json");
            post.setEntity(new StringEntity(array.toString(), "UTF-8"));
            HttpResponse response = HTTPCLIENT.execute(post);
            StringBuffer result = new StringBuffer();
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "utf-8"));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            JSONArray ja = JSONArray.parseArray(result.toString()).getJSONArray(0);
            JSONObject jo = ja.getJSONObject(0);

            String tmp_result = ((String) jo.get("tgt")).replace(" ", "");
            last_result += tmp_result + "\n";
        }

        return last_result;

    }


    public String segmentation(String text) {
        ISentenceSegmenter segmenter = new RuleSentenceSegmenter();
        List<StringBuffer> spaces = new ArrayList<>();
        List<Rule> brules = new ArrayList<>();
        List<String> lineList = segmenter.segment(LanguageInfoUtils.getSLLangByName("vi"), text, spaces, brules);
        for (String line : lineList) {
            System.out.println(line);
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : lineList) {
            stringBuffer.append(s + "$#$");
        }
        return stringBuffer.toString();
    }

}
