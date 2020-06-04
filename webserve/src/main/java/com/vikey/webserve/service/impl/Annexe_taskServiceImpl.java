package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.*;
import com.vikey.webserve.mapper.Annexe_taskMapper;
import com.vikey.webserve.service.IAnnexeService;
import com.vikey.webserve.service.IAnnexe_taskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vikey.webserve.service.IAtask_annService;
import com.vikey.webserve.service.IFast_taskService;
import com.vikey.webserve.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private IAnnexeService iAnnexeService;

    @Resource
    private IAtask_annService iAtaskAnnService;

    @Resource
    private IFast_taskService iFast_taskService;

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
            String a_type = obj.getString("type");
            Annexe annexe = new Annexe();
            annexe.setCreate_time(now);
            annexe.setName(a_name);
            annexe.setPath(a_path);
            annexe.setType(a_type);
            annexe.setDiscard(Constant.NOT_DELETE);
            annexe.setOriginal_language(language);
            annexe.setStatus(Constant.ANNEXE_STATUS_UNPROCESSED);
            annexes.add(annexe)
            ;

        }
        iAnnexeService.saveBatch(annexes);
        List<Atask_ann> atask_anns = new ArrayList<>();
        annexes.stream().forEach(a -> {
            Atask_ann atask_ann = new Atask_ann();
            atask_ann.setAtid(atid);
            atask_ann.setAid(a.getId());
            atask_anns.add(atask_ann);
        });
        iAtaskAnnService.saveBatch(atask_anns);
    }

    @Override
    public Map getAllTaskCount(Long id) {
        return getBaseMapper().getAllTaskCount(id);
    }

    @Override
    public String getMostUseLanguage(Long id) {
        List<Map> ats = getBaseMapper().getMostAtUseLanguage(id);
        List<Map> fts = ((Fast_taskServiceImpl) iFast_taskService).getBaseMapper().getMostFtUseLanguage(id);
        Map<String, Integer> ats_map = ats.stream().collect(Collectors.toMap(v -> (String) v.get("original_language"), v -> Integer.valueOf(v.get("count").toString())));
        Map<String, Integer> fts_map = fts.stream().collect(Collectors.toMap(v -> (String) v.get("original_language"), v -> Integer.valueOf(v.get("count").toString())));
        Map<String, Integer> result = Stream.concat(ats_map.entrySet().stream(), fts_map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                        , (value1, value2) -> value1 + value2));
        Optional<Map.Entry<String, Integer>> max = result.entrySet().stream().max(Map.Entry.comparingByValue());
        return max.get().getKey();
    }
}
