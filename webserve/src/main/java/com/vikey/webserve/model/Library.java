package com.vikey.webserve.model;

import com.baomidou.mybatisplus.annotation.*;
import com.vikey.webserve.Constant;

import java.io.Serializable;
import java.util.Date;

/**
 * 关键词库实体类
 */
@TableName("library")
public class Library implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "discard")
    @TableLogic(value = Constant.NOT_DELETE, delval = Constant.DELETE)
    private String discard;//逻辑删除标识

    @TableField("create_time")
    private Date createTime;

    @TableField("name")
    private String name;

    @TableField("uid")
    private Long uid;//用户id

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
