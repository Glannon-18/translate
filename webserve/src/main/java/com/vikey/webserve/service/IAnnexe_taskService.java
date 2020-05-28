package com.vikey.webserve.service;

import com.alibaba.fastjson.JSONObject;
import com.vikey.webserve.entity.Annexe_task;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18exe
 */
public interface IAnnexe_taskService extends IService<Annexe_task> {

    /**
     * 查询同一天下的快速翻译任务，左侧菜单栏
     *
     * @param uid 用户id
     * @return
     */
    LinkedHashMap<String, List<Annexe_task>> getAnnexe_taskByDate(Long uid, String name);


    void createAnnexe_task(JSONObject jsonObject);
}
