package com.vikey.webserve.service;

import com.vikey.webserve.entity.Fast_task;
import com.baomidou.mybatisplus.extension.service.IService;

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
    public List<Fast_task> getLastFast_task(Long uid);

    /**
     * 查询同一天下的快速翻译任务，左侧菜单栏
     *
     * @param uid 用户id
     * @return
     */
    public LinkedHashMap<String, List<Fast_task>> getFast_taskByDate(Long uid);


}
