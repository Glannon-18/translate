package com.vikey.webserve.service;

import java.util.Map;

public interface TranslateService {

    Map<String,Object> translate(String text, String srcLang, String tgtLang);

}
