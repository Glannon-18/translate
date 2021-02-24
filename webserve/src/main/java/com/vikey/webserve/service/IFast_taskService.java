package com.vikey.webserve.service;

import com.alibaba.fastjson.JSONObject;
import com.vikey.webserve.entity.Fast_task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vikey.webserve.entity.RespBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface IFast_taskService extends IService<Fast_task> {

    /**
     * 历史记录，查询最后五条快速翻译记录
     *
     * @param uid 用户id
     * @return
     */
    List<Fast_task> getLastFast_task(Long uid);

    /**
     * 查询同一天下的快速翻译任务，左侧菜单栏
     *
     * @param uid 用户id
     * @return
     */
    LinkedHashMap<String, List<Fast_task>> getFast_taskByDate(Long uid, String name);

    /**
     * 根据id查询实体，调用baseMapper里面的方法
     *
     * @param id
     * @return
     */
    Fast_task getFast_TaskById(Long id);


    String translate(String text, String srcLang, String tgtLang) throws Exception;


    String translate_xiaoniu(String text, String from, String to) throws Exception;


    String translate_service(String text, String srcLang, String tgtLang) throws IOException;

    String qwqq(String text);


    LocalDateTime minDateTime();


}
