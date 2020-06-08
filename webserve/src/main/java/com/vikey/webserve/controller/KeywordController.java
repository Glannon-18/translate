package com.vikey.webserve.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.Keyword;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.service.IKeywordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/keyword")
public class KeywordController {

    @Resource
    private IKeywordService iKeywordService;

    @GetMapping("/")
    public RespBean listKeyword(@RequestParam String lid) {
        QueryWrapper<Keyword> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "word").eq("lid", Long.valueOf(lid)).eq("discard", Constant.NOT_DELETE);
        List<Keyword> list = iKeywordService.list(queryWrapper);
        return RespBean.ok(list);
    }

    @PostMapping("/")
    public RespBean createKeyword(@RequestParam String lib, @RequestParam String content) {
        Keyword keyword = iKeywordService.create(lib, content);
        return RespBean.ok(keyword);
    }

    @DeleteMapping("/{id}")
    public RespBean deleteKeyword(@PathVariable String id) {
        iKeywordService.delete(id);
        return RespBean.ok();
    }

}
