package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pingsoft.segmentation.ISentenceSegmenter;
import com.pingsoft.segmentation.Rule;
import com.pingsoft.segmentation.RuleSentenceSegmenter;
import com.pingsoft.translate.utils.LanguageInfoUtils;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.service.TranslateService;
import org.apache.commons.lang.StringUtils;
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
import java.util.stream.Collectors;

@Component
public class XiaoNiuTranslateService implements TranslateService {


    /**
     * 单次请求最多2000字符
     */
    public static final int MAX_LENGTH = 2000;

    private static HttpClient HTTPCLIENT = HttpClientBuilder.create().build();

    @Resource
    private PersonalConfig personalConfig;


    @Override
    public Map<String, Object> translate(String text, String srcLang, String tgtLang) {
        HashMap<String, Object> returnMap = new HashMap<>();

        List<String> segmentation = segmentation(text);
        String result = "";
        for (String sentence : segmentation) {

            JSONObject input = new JSONObject();
            input.put("from", srcLang);
            input.put("to", tgtLang);
            input.put("src_text", sentence);
            input.put("apikey", personalConfig.getApiKey_xiaoniu());


            StringEntity info = new StringEntity(input.toString(), "utf-8");
            info.setContentEncoding("utf-8");
            HttpPost httpPost = new HttpPost(personalConfig.getTranslate_api_url_xiaoniu());
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(info);
            try {
                HttpResponse httpResponse = HTTPCLIENT.execute(httpPost);
                if ((httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)) {
                    String temp = EntityUtils.toString(httpResponse.getEntity());
                    JSONObject jsonObject = JSONObject.parseObject(temp);
                    if ((jsonObject.containsKey("error_code"))) {
                        returnMap.put("code", 500);
                        returnMap.put("message", "翻译接口返回错误信息：错误码：" +
                                jsonObject.getString("error_code") + "，错误信息：" +
                                jsonObject.getString("error_msg"));
                        return returnMap;
                    } else {
                        result += jsonObject.getString("tgt_text");
                    }
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
        data.put("tgtText", result);

        returnMap.put("data", data);
        returnMap.put("code", 0);
        returnMap.put("message", "");
        return returnMap;
    }


    //简单的英文分句
    private List<String> segmentation(String text) {

        ISentenceSegmenter segmenter = new RuleSentenceSegmenter();
        List<StringBuffer> spaces = new ArrayList<>();
        List<Rule> brules = new ArrayList<>();
        List<String> lineList = segmenter.segment(LanguageInfoUtils.getSLLangByName("en"), text, spaces, brules);
        ArrayList<String> all = new ArrayList<>();
        String temp = "";
        for (int i = 0; i < lineList.size(); i++) {
            if ((temp + lineList.get(i)).length() <= MAX_LENGTH) {
                temp += lineList.get(i);
            } else {
                all.add(temp);
                temp = lineList.get(i);
            }

            if (i == lineList.size() - 1) {
                all.add(temp);
            }
        }
        return all.stream().filter(t -> !StringUtils.isEmpty(t)).collect(Collectors.toList());
    }

}
