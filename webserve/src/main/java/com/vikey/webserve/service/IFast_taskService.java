package com.vikey.webserve.service;

import com.vikey.webserve.entity.Fast_task;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface IFast_taskService extends IService<Fast_task> {

    public List<Fast_task> getLastFast_task(Long id);

    public LinkedHashMap<String,List<Fast_task>> getFast_taskByDate(Long id);


}
