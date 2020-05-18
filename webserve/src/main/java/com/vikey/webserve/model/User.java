package com.vikey.webserve.model;

import com.baomidou.mybatisplus.annotation.*;
import com.vikey.webserve.Constant;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户实体类
 */
@TableName("user")
public class User implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "discard")
    @TableLogic(value = Constant.NOT_DELETE, delval = Constant.DELETE)
    private String discard;//逻辑删除标识

    @TableField("create_time")
    private Date createTime;

    @TableField("account")
    private String account;

    @TableField("password")
    private String password;

    @TableField("username")
    private String username;

    @TableField("last_login_time")
    private Date lastLoginTime;

    @TableField("telephone")
    private String telephone;

    @TableField("enabled")
    private boolean enabled;


    @TableField("userface")
    private String userface;

    @TableField(exist = false)
    private List<Role> roleList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String isDiscard() {
        return discard;
    }

    public void setDiscard(String discard) {
        this.discard = discard;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUserface() {
        return userface;
    }

    public void setUserface(String userface) {
        this.userface = userface;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
