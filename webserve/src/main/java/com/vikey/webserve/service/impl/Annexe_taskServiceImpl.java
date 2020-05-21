package com.vikey.webserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.entity.Annexe_task;
import com.vikey.webserve.entity.Fast_task;
import com.vikey.webserve.mapper.Annexe_taskMapper;
import com.vikey.webserve.service.IAnnexe_taskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class Annexe_taskServiceImpl extends ServiceImpl<Annexe_taskMapper, Annexe_task> implements IAnnexe_taskService {

    @Override
    public LinkedHashMap<String, List<Annexe_task>> getAnnexe_taskByDate(Long uid, String name) {
        List<Annexe_task> annexe_taskList = getBaseMapper().getAnnexe_taskByDate(uid, name);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LinkedHashMap<String, List<Annexe_task>> map = new LinkedHashMap<>();
        annexe_taskList.stream().forEach(t -> {
            String date = dateTimeFormatter.format(t.getCreate_time());
            if (map.containsKey(date)) {
                map.get(date).add(t);
            } else {
                List<Annexe_task> list = new ArrayList<>();
                list.add(t);
                map.put(date, list);
            }
        });
        return map;
    }
}
