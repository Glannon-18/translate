package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.Library;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.RespPageBean;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.service.ILibraryService;
import com.vikey.webserve.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/library")
public class LibraryController {


    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);


    @Resource
    private ILibraryService iLibraryService;

    @PostMapping("/")
    public RespBean createLib(@RequestBody JSONObject jsonObject) {
        User user = SecurityUtils.getCurrentUser();
        String name = jsonObject.getString("name");
        Library library = new Library();
        library.setCreate_time(LocalDateTime.now());
        library.setDiscard(Constant.NOT_DELETE);
        library.setName(name);
        library.setUid(user.getId());
        iLibraryService.save(library);
        return RespBean.ok("ok");
    }

    @DeleteMapping("/")
    public RespBean deleteLib(@RequestParam String id) {
        UpdateWrapper<Library> libraryUpdateWrapper = new UpdateWrapper<>();
        libraryUpdateWrapper.set("discard", Constant.DELETE).eq("id", Long.valueOf(id));
        iLibraryService.update(libraryUpdateWrapper);
        return RespBean.ok("ok");
    }

    @GetMapping("/")
    public RespPageBean pageLib(@RequestParam String name, @RequestParam String currentPage) {
        User user = SecurityUtils.getCurrentUser();
        Page<Library> page = new Page<>(Integer.valueOf(currentPage), Constant.PAGESIZE);
        IPage<Library> iPage = iLibraryService.list(page, user.getId(), name);
        RespPageBean respPageBean = new RespPageBean(iPage.getTotal(), iPage.getRecords(), iPage.getSize());
        return respPageBean;
    }


}
