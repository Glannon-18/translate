package com.vikey.webserve.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vikey.webserve.Constant;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.entity.User_role;
import com.vikey.webserve.mapper.UserMapper;
import com.vikey.webserve.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vikey.webserve.service.IUser_roleService;
import org.apache.catalina.realm.UserDatabaseRealm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Resource
    private IUser_roleService iUser_roleService;

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
    @Transactional(rollbackFor = Exception.class)
    public void create(JSONObject jsonObject) {
        String account = jsonObject.getString("account");
        String username = jsonObject.getString("username");
        String phone = jsonObject.getString("phone");
        ArrayList<Integer> roles = (ArrayList<Integer>) jsonObject.get("roles");
        User user = new User();
        user.setAccount(account);
        user.setCreate_time(LocalDateTime.now());
        user.setDiscard(Constant.NOT_DELETE);
        user.setAvailable(true);
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setTelephone(phone);
        user.setUsername(username);

        getBaseMapper().insert(user);
        Long userid = user.getId();
        List<User_role> user_roleList = new ArrayList<>();
        roles.stream().forEach(r -> {
            User_role ur = new User_role();
            ur.setRid(Long.valueOf(r));
            ur.setUid(userid);
            user_roleList.add(ur);
        });
        iUser_roleService.saveBatch(user_roleList);
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
