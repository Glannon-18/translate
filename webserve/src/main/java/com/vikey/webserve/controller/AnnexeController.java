package com.vikey.webserve.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.entity.RespBean;
import com.vikey.webserve.entity.RespPageBean;
import com.vikey.webserve.service.IAnnexeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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
@RequestMapping("/annexe")
public class AnnexeController {

    @Resource
    private IAnnexeService IAnnexeService;


    @GetMapping("/page")
    public RespPageBean getAnnexePage(@RequestParam String currentPage, @RequestParam String atid) {
        IPage<Annexe> page = IAnnexeService.getAnnexeByPage(Integer.valueOf(currentPage), Constant.PAGESIZE, Long.valueOf(atid));
        return new RespPageBean(page.getTotal(),page.getRecords(),page.getSize());
    }

    @DeleteMapping("/")
    public RespBean deleteAnnexe(@RequestBody String ids){
        JSONObject object=JSONObject.parseObject(ids);
        JSONArray array= object.getJSONArray("ids");
        UpdateWrapper<Annexe> updateWrapper=new UpdateWrapper<>();
        updateWrapper.set("discard",Constant.DELETE).in("id",array);
        IAnnexeService.getBaseMapper().update(null,updateWrapper);
        return RespBean.ok("ok");
    }

}
