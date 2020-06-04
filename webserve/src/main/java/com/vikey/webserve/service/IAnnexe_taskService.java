package com.vikey.webserve.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vikey.webserve.entity.Annexe_task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18exe
 */
public interface IAnnexe_taskService extends IService<Annexe_task> {


    LinkedHashMap<String, List<Annexe_task>> getAnnexe_taskByDate(Long uid, String name);


    void createAnnexe_task(JSONObject jsonObject);


    Map getAllTaskCount(Long id);


    String getMostUseLanguage(Long id);


    String getLastUseLanguage(Long id);
}
