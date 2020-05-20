package com.vikey.webserve.service;

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

}
