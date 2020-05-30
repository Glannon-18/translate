package com.vikey.webserve.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vikey.webserve.entity.User;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface IUserService extends IService<User> {

    /**
     * 获取一个用户包括角色信息
     *
     * @param id 用户id
     * @return
     */
    User selectUserWithRoles(Long id);

    /**
     * @param account 登陆账号名
     * @return
     */
    User selectUserWithRolesByAccount(String account);


    void create(JSONObject jsonObject);


    IPage<User> selectUserWithRolesByName(Page<User> page, String name);


    void update(String id, JSONObject jsonObject);

}
