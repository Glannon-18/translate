package com.vikey.webserve.model;

import com.baomidou.mybatisplus.annotation.*;
import com.vikey.webserve.Constant;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色实体类
 */
@TableName("role")
public class Role implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "discard")
    @TableLogic(value = Constant.NOT_DELETE, delval = Constant.DELETE)
    private String discard;//逻辑删除标识

    @TableField("create_time")
    private Date createTime;

    @TableField("name")
    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
