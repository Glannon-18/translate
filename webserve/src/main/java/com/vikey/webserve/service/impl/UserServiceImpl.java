package com.vikey.webserve.service.impl;

import com.vikey.webserve.entity.User;
import com.vikey.webserve.mapper.UserMapper;
import com.vikey.webserve.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.catalina.realm.UserDatabaseRealm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService, UserDetailsService {

    @Override
    public User selectUserWithRoles(Long id) {
        User user = super.getBaseMapper().selectUserWithRoles(1l);
        return user;
    }

    @Override
    public User selectUserWithRolesByAccount(String account) {
        User user = super.baseMapper.selectUserWithRolesByAccount(account);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = selectUserWithRolesByAccount(username);
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("账号或用户名错误！");
        }
        return user;
    }
}
