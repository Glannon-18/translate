package com.vikey.webserve.service;

import com.vikey.webserve.entity.Annexe;

import java.util.List;

/**
 * 异步任务接口
 */
public interface IAsyncService {

     void translate(Annexe annexe, String srcLang, String tgtLang);
}
