package com.vikey.webserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * <p>
 *     用户角色中间表实体类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User_role implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private Long rid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "User_role{" +
            "id=" + id +
            ", uid=" + uid +
            ", rid=" + rid +
        "}";
    }
}
