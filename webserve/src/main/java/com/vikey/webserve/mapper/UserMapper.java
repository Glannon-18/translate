package com.vikey.webserve.mapper;

import com.vikey.webserve.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface UserMapper extends BaseMapper<User> {


    /**
     * 根据id获取用户
     *
     * @param id
     * @return
     */
    User selectUserWithRoles(@Param("id") Long id);

    /**
     * 根据账号名获取用户
     *
     * @param account
     * @return
     */
    User selectUserWithRolesByAccount(@Param("account") String account);

}
