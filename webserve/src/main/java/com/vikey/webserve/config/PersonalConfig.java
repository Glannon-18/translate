package com.vikey.webserve.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "personal-config")
public class PersonalConfig {

    private String make_file_dir;

    private String upload_dir;

    private String translate_api_url;

    private String translate_dir;

    private String translate_api_url_xiaoniu;

    private String apiKey_xiaoniu;

    private String word_img_dir;

    private String local_ip;

    public String getLocal_ip() {
        return local_ip;
    }

    public void setLocal_ip(String local_ip) {
        this.local_ip = local_ip;
    }

    public String getTranslate_dir() {
        return translate_dir;
    }

    public void setTranslate_dir(String translate_dir) {
        this.translate_dir = translate_dir;
    }

    public String getMake_file_dir() {
        return make_file_dir;
    }

    public void setMake_file_dir(String make_file_dir) {
        this.make_file_dir = make_file_dir;
    }

    public String getUpload_dir() {
        return upload_dir;
    }

    public void setUpload_dir(String upload_dir) {
        this.upload_dir = upload_dir;
    }

    public String getTranslate_api_url_xiaoniu() {
        return translate_api_url_xiaoniu;
    }

    public void setTranslate_api_url_xiaoniu(String translate_api_url_xiaoniu) {
        this.translate_api_url_xiaoniu = translate_api_url_xiaoniu;
    }

    public String getApiKey_xiaoniu() {
        return apiKey_xiaoniu;
    }

    public void setApiKey_xiaoniu(String apiKey_xiaoniu) {
        this.apiKey_xiaoniu = apiKey_xiaoniu;
    }

    public String getWord_img_dir() {
        return word_img_dir;
    }

    public void setWord_img_dir(String word_img_dir) {
        this.word_img_dir = word_img_dir;
    }

    public String getTranslate_api_url() {
        return translate_api_url;
    }

    public void setTranslate_api_url(String translate_api_url) {
        this.translate_api_url = translate_api_url;
    }
}
