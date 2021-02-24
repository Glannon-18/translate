package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.entity.Annexe_task;
import com.vikey.webserve.entity.Atask_ann;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.mapper.Annexe_taskMapper;
import com.vikey.webserve.service.*;
import com.vikey.webserve.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
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

    @Resource
    private IUserService iUserService;


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
    public List<Annexe> createAnnexe_task(JSONObject jsonObject) {
        User user = SecurityUtils.getCurrentUser();
        String name = jsonObject.getString("name");
        String language = jsonObject.getString("language");
        String language_tra = jsonObject.getString("language_tra");
        JSONArray paths = jsonObject.getJSONArray("filePaths");

        LocalDateTime now = LocalDateTime.now();

        Annexe_task annexe_task = new Annexe_task();
        annexe_task.setCreate_time(now);
        annexe_task.setDiscard(Constant.NOT_DELETE);
        annexe_task.setName(name);
        annexe_task.setOriginal_language(language);
        annexe_task.setTranslate_language(language_tra);
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
            annexes.add(annexe);
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

        return annexes;

    }

    @Override
    public Map getAllTaskCount(Long id, LocalDateTime time) {
        return getBaseMapper().getAllTaskCount(id, time);
    }

    @Override
    public String getMostUseLanguage(Long id, LocalDateTime time) {
        List<Map> ats = getBaseMapper().getMostAtUseLanguage(id, time);
        List<Map> fts = ((Fast_taskServiceImpl) iFast_taskService).getBaseMapper().getMostFtUseLanguage(id, time);
        Map<String, Integer> ats_map = ats.stream().collect(Collectors.toMap(v -> (String) v.get("original_language"), v -> Integer.valueOf(v.get("count").toString())));
        Map<String, Integer> fts_map = fts.stream().collect(Collectors.toMap(v -> (String) v.get("original_language"), v -> Integer.valueOf(v.get("count").toString())));
        Map<String, Integer> result = Stream.concat(ats_map.entrySet().stream(), fts_map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                        , (value1, value2) -> value1 + value2));
        Optional<Map.Entry<String, Integer>> max = result.entrySet().stream().max(Map.Entry.comparingByValue());
        if (max.isPresent()) {
            return max.get().getKey();
        } else {
            return "";
        }
    }

    @Override
    public String getLastUseLanguage(Long id, LocalDateTime after) {
        Map<String, Object> ft = ((Fast_taskServiceImpl) iFast_taskService).getBaseMapper().getLastFtUseLanguage(id, after);
        Map<String, Object> at = getBaseMapper().getLastAtUseLanguage(id, after);
        if (ft == null && at == null) {
            return "";
        } else if (ft == null && at != null) {
            return at.get("original_language").toString();
        } else if (ft != null && at == null) {
            return ft.get("original_language").toString();
        } else {
            Timestamp ft_time = (Timestamp) ft.get("time");
            Timestamp at_time = (Timestamp) at.get("time");
            return ft_time.compareTo(at_time) > 0 ? ft.get("original_language").toString() : at.get("original_language").toString();
        }
    }

    @Override
    public List<Map> getAllInfo(LocalDateTime after) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<User> users = iUserService.list(userQueryWrapper.select("id", "account"));
        List<Map> result = users.stream().map(t -> {
            Long uid = t.getId();
            String account = t.getAccount();
            Map<String, Object> map = new HashMap<>();
            map.put("username", account);
            Integer task_count = (Integer) getAllTaskCount(uid, after).get("count");
            map.put("task_count", task_count);
            Integer annexe_count = iAnnexeService.getAnnexeCountByUserid(uid, after);
            map.put("annexe_count", annexe_count);
            String most_use_language = getMostUseLanguage(uid, after);
            map.put("most_use_language", StringUtils.isEmpty(most_use_language) ? "" : Constant.LANGUAGE_ZH.get(most_use_language));
            String last_use_language = getLastUseLanguage(uid, after);
            map.put("last_use_language", StringUtils.isEmpty(last_use_language) ? "" : Constant.LANGUAGE_ZH.get(last_use_language));
            return map;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<Map<String, String>> getCountByLanguage(LocalDateTime after) {
        List<Map> ft = ((Fast_taskServiceImpl) iFast_taskService).getBaseMapper().getCountFtByLanguage(after);
        List<Map> at = getBaseMapper().getCountAtByLanguage(after);

        Map<String, Integer> ats_map = ft.stream().collect(Collectors.toMap(v -> (String) v.get("original_language"), v -> Integer.valueOf(v.get("count").toString())));
        Map<String, Integer> fts_map = at.stream().collect(Collectors.toMap(v -> (String) v.get("original_language"), v -> Integer.valueOf(v.get("count").toString())));
        Map<String, Integer> data = Stream.concat(ats_map.entrySet().stream(), fts_map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                        , (value1, value2) -> value1 + value2));
        List<Map<String, String>> result = data.entrySet().stream().map((k) -> new HashMap<String, String>() {{
            put("name", Constant.LANGUAGE_ZH.get(k.getKey()));
            put("value", k.getValue().toString());
            put("color", Constant.LANGUAGE_COLOR.get(k.getKey()));
        }}).collect(Collectors.toList());
        return result;
    }

    @Override
    public LocalDateTime minDateTime() {
        return getBaseMapper().minDateTime();
    }


}
