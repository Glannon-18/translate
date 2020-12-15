package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pingsoft.segmentation.ISentenceSegmenter;
import com.pingsoft.segmentation.Rule;
import com.pingsoft.segmentation.RuleSentenceSegmenter;
import com.pingsoft.translate.utils.LanguageInfoUtils;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.service.TranslateService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PingSoftTranslateService implements TranslateService {

    private static HttpClient HTTPCLIENT = HttpClientBuilder.create().build();

    @Resource
    private PersonalConfig personalConfig;

    @Override
    public Map<String, Object> translate(String text, String srcLang, String tgtLang) {
        HashMap<String, Object> returnMap = new HashMap<>();

        String[] strings = text.split("\n");
        String last_result = "";
        for (String string : strings) {
            string = string.trim().replaceAll("\"", "\\\"");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", 300);
            String segmentation = segmentation(string);
            jsonObject.put("src", segmentation);
            JSONArray array = new JSONArray();
            array.add(jsonObject);

            HttpPost post = new HttpPost(personalConfig.getTranslate_api_url());
            post.setHeader("Content-type", "application/json");
            post.setEntity(new StringEntity(array.toString(), "UTF-8"));
            try {
                HttpResponse response = HTTPCLIENT.execute(post);
                if ((response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)) {
                    String temp = EntityUtils.toString(response.getEntity());
                    JSONArray temp_result = JSONArray.parseArray(temp);
                    JSONObject jo = temp_result.getJSONArray(0).getJSONObject(0);
                    String str = ((String) jo.get("tgt")).replace(" ", "");

                    last_result += str;

                } else {
                    returnMap.put("code", 500);
                    returnMap.put("message", "发送请求时错误");
                    return returnMap;
                }
            } catch (IOException e) {
                e.printStackTrace();

                returnMap.put("code", 500);
                returnMap.put("message", "IO错误");
                return returnMap;
            }

        }
        HashMap<String, String> data = new HashMap<>();
        data.put("srcLang", srcLang);
        data.put("tgtLang", tgtLang);
        data.put("tgtText", last_result);

        returnMap.put("data", data);
        returnMap.put("code", 0);
        returnMap.put("message", "");

        return returnMap;

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
