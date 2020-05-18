package com.vikey.webserve.service.impl;

import com.vikey.webserve.entity.Role;
import com.vikey.webserve.mapper.RoleMapper;
import com.vikey.webserve.service.IRoleService;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
