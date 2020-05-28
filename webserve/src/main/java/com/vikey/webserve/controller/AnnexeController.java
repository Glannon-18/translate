package com.vikey.webserve.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vikey.webserve.Constant;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.RespPageBean;
import com.vikey.webserve.service.IAnnexeService;
import com.vikey.webserve.utils.ZipUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.function.Function;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/annexe")
public class AnnexeController {

    @Resource
    private PersonalConfig PersonalConfig;


    @Resource
    private IAnnexeService IAnnexeService;


    @GetMapping("/page")
    public RespPageBean getAnnexePage(@RequestParam String currentPage, @RequestParam String atid) {
        IPage<Annexe> page = IAnnexeService.getAnnexeByPage(Integer.valueOf(currentPage), Constant.PAGESIZE, Long.valueOf(atid));
        return new RespPageBean(page.getTotal(), page.getRecords(), page.getSize());
    }

    @DeleteMapping("/")
    public RespBean deleteAnnexe(@RequestParam String ids) {
        UpdateWrapper<Annexe> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("discard", Constant.DELETE).in("id", convert(ids));
        IAnnexeService.getBaseMapper().update(null, updateWrapper);
        return RespBean.ok("ok");
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam String ids) {
        List<Long> list = convert(ids);
        QueryWrapper<Annexe> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name", "path").in("id", list);
        List<Annexe> annexeList = IAnnexeService.getBaseMapper().selectList(queryWrapper);

        try {
            File file = new File(PersonalConfig.getMake_file_dir() + File.separator + "a.zip");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(PersonalConfig.getMake_file_dir() + File.separator + "a.zip");
            ZipUtils.toZip(annexeList, fileOutputStream, PersonalConfig.getUpload_dir());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }


    private List<Long> convert(String content) {
        List<Long> list = new ArrayList<>();
        Arrays.stream(content.split(",")).forEach(i ->
        {
            list.add(Long.valueOf(i));
        });
        return list;
    }

}
