package com.vikey.webserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.Keyword;
import com.vikey.webserve.mapper.KeywordMapper;
import com.vikey.webserve.service.IKeywordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@Service
public class KeywordServiceImpl extends ServiceImpl<KeywordMapper, Keyword> implements IKeywordService {

    @Override
    @Transactional
    public Keyword create(String lid, String word) {
        LocalDateTime now = LocalDateTime.now();
        Keyword keyword = new Keyword();
        keyword.setCreate_time(now);
        keyword.setDiscard(Constant.NOT_DELETE);
        keyword.setLid(Long.valueOf(lid));
        keyword.setWord(word);
        save(keyword);
        return keyword;

    }

    @Override
    @Transactional
    public void delete(String id) {
        UpdateWrapper<Keyword> keywordUpdateWrapper = new UpdateWrapper<>();
        keywordUpdateWrapper.set("discard", Constant.DELETE).eq("id", Long.valueOf(id));
        update(keywordUpdateWrapper);
    }
}
