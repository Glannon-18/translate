package com.vikey.webserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.mapper.AnnexeMapper;
import com.vikey.webserve.service.IAnnexeService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@Service
public class AnnexeServiceImpl extends ServiceImpl<AnnexeMapper, Annexe> implements IAnnexeService {

    @Resource
    private PersonalConfig personalConfig;


    @Override
    public IPage<Annexe> getAnnexeByPage(Integer currentPage, Integer pageSize, Long atid) {
        Page<Annexe> page = new Page<>(currentPage, pageSize);
        IPage<Annexe> iPage = getBaseMapper().getAnnexeByAtid(page, atid);
        return iPage;
    }

    @Override
    public Integer getAnnexeCount(LocalDateTime time, String status) {
        return getBaseMapper().getAnnexeCount(time, status);
    }

    @Override
    public List<Map> getAnnexeCountByPeriod(List<LocalDateTime> periods, String type, String format) {
        LocalDateTime start = periods.get(0);
        return getBaseMapper().getAnnexeCountByPeriod(periods, type, format, start);
    }

    @Override
    public Integer getAnnexeCountByUserid(Long userid, LocalDateTime after) {
        return getBaseMapper().getAnnexeCountByUserid(userid, after);
    }

    @Override
    public List<Map> getAnnexeCountByType(LocalDateTime time) {
        List<Map> list = getBaseMapper().getAnnexeCountByType(time);
        BigDecimal total = list.stream().reduce(new BigDecimal("0"), (a, b) -> a.add((BigDecimal) b.get("count")), (a, b) -> null);
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        if (total.equals(BigDecimal.ZERO)) {
            list.stream().forEach(t -> {
                t.put("rate", percent.format(total.doubleValue()));
            });
        } else {
            list.stream().forEach(t -> {
                BigDecimal count = (BigDecimal) t.get("count");
                BigDecimal rate = count.divide(total, 2, BigDecimal.ROUND_HALF_UP);
                t.put("rate", percent.format(rate.doubleValue()));
            });
        }
        return list;
    }

    @Override
    public Annexe getAnnexeById(Long id) throws IOException {
        QueryWrapper<Annexe> annexeQueryWrapper = new QueryWrapper<>();
        annexeQueryWrapper.select("id", "name", "path", "translate_path", "type").eq("id", id);
        Annexe annexe = getOne(annexeQueryWrapper);
        File original_file = new File(personalConfig.getUpload_dir() + File.separator + annexe.getPath());
        File translate_file = new File(personalConfig.getTranslate_dir() + File.separator + annexe.getTranslate_path());
        if (annexe.getType().equals("txt")) {
            annexe.setOriginal_text(FileUtils.readFileToString(original_file, "utf-8"));
            annexe.setTranslate_text(FileUtils.readFileToString(translate_file, "utf-8"));
            annexe.setPath(null);
            annexe.setTranslate_path(null);
        }
        return annexe;
    }

}
