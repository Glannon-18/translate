package com.vikey.webserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.entity.Fast_task;
import com.vikey.webserve.mapper.Fast_taskMapper;
import com.vikey.webserve.service.IFast_taskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@Service
public class Fast_taskServiceImpl extends ServiceImpl<Fast_taskMapper, Fast_task> implements IFast_taskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Fast_taskServiceImpl.class);


    @Override
    public List<Fast_task> getLastFast_task(Long id) {
        List<Fast_task> fast_taskList;
        fast_taskList = getBaseMapper().selectLastFast_task(id);
        return fast_taskList;
    }

    @Override
    public LinkedHashMap<String, List<Fast_task>> getFast_taskByDate(Long id) {
        LinkedHashMap<String, List<Fast_task>> map = new LinkedHashMap<>();
        List<Fast_task> fast_taskList;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        fast_taskList = getBaseMapper().selectFast_taskByDate(id);
        fast_taskList.stream().forEach(t -> {
            String date = dateTimeFormatter.format(t.getCreate_time());
            if (map.containsKey(date)) {
                map.get(date).add(t);
            } else {
                List<Fast_task> list = new ArrayList<>();
                list.add(t);
                map.put(date, list);
            }
        });
        return map;
    }


}
