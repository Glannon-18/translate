package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.*;
import com.vikey.webserve.mapper.AnnexeMapper;
import com.vikey.webserve.mapper.Annexe_taskMapper;
import com.vikey.webserve.service.IAnnexeService;
import com.vikey.webserve.service.IAnnexe_taskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vikey.webserve.service.IAtask_annService;
import com.vikey.webserve.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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


    private static final Logger LOGGER = LoggerFactory.getLogger(Annexe_taskServiceImpl.class);

    @Resource
    private IAnnexeService IAnnexeService;

    @Resource
    private IAtask_annService IAtask_annService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAnnexe_task(JSONObject jsonObject) {
        User user = SecurityUtils.getCurrentUser();
        String name = jsonObject.getString("name");
        String language = jsonObject.getString("language");
        JSONArray paths = jsonObject.getJSONArray("filePaths");

        LocalDateTime now = LocalDateTime.now();

        Annexe_task annexe_task = new Annexe_task();
        annexe_task.setCreate_time(now);
        annexe_task.setDiscard(Constant.NOT_DELETE);
        annexe_task.setName(name);
        annexe_task.setOriginal_language(language);
        annexe_task.setUid(user.getId());
        save(annexe_task);
        Long atid = annexe_task.getId();

        List<Annexe> annexes = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            JSONObject obj = paths.getJSONObject(i);
            String a_name = obj.getString("originalName");
            String a_path = obj.getString("severName");
            Annexe annexe = new Annexe();
            annexe.setCreate_time(now);
            annexe.setName(a_name);
            annexe.setPath(a_path);
            annexe.setOriginal_language(language);
            annexe.setStatus(Constant.ANNEXE_STATUS_UNPROCESSED);
            annexes.add(annexe)
            ;

        }
        IAnnexeService.saveBatch(annexes);
        List<Atask_ann> atask_anns = new ArrayList<>();
        annexes.stream().forEach(a -> {
            Atask_ann atask_ann = new Atask_ann();
            atask_ann.setAtid(atid);
            atask_ann.setAid(a.getId());
            atask_anns.add(atask_ann);
        });
        IAtask_annService.saveBatch(atask_anns);
    }
}
