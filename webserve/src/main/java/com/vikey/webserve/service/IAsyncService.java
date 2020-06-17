package com.vikey.webserve.service;

import com.vikey.webserve.entity.Annexe;

import java.util.List;

/**
 * 异步任务接口
 */
public interface IAsyncService {

    void translate(List<Annexe> annexes, String srcLang, String tgtLang);


//    void folder_translate(String src, String tgt, String src_lan, String tat_lan);
}
