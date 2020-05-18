package com.vikey.webserve.service.impl;

import com.vikey.webserve.entity.User;
import com.vikey.webserve.mapper.UserMapper;
import com.vikey.webserve.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
