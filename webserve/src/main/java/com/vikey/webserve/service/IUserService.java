package com.vikey.webserve.service;

import com.vikey.webserve.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface IUserService extends IService<User> {

    User selectUserWithRoles(Long id);

}
