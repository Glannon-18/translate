package com.vikey.webserve.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "personal-config")
public class PersonalConfig {

    private String make_file_dir;

    public String getMake_file_dir() {
        return make_file_dir;
    }

    public void setMake_file_dir(String make_file_dir) {
        this.make_file_dir = make_file_dir;
    }
}
