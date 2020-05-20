package com.vikey.webserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String discard;

    private LocalDateTime create_time;

    private String account;

    private String password;

    private String username;

    private LocalDateTime last_login_time;

    private String telephone;

    private Boolean enabled;

    /**
     * 用户头像地址
     */
    private String userface;

    @TableField(exist = false)
    private List<Role> roleList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiscard() {
        return discard;
    }

    public void setDiscard(String discard) {
        this.discard = discard;
    }

    public LocalDateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDateTime create_time) {
        this.create_time = create_time;
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

    public LocalDateTime getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(LocalDateTime last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", discard=" + discard +
                ", create_time=" + create_time +
                ", account=" + account +
                ", password=" + password +
                ", username=" + username +
                ", last_login_time=" + last_login_time +
                ", telephone=" + telephone +
                ", enabled=" + enabled +
                ", userface=" + userface +
                "}";
    }
}
