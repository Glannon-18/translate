package com.vikey.webserve.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("atask_ann")
public class ATask_Ann implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("atid")
    private Long atid;
    
    @TableField("aid")
    private Long aid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAtid() {
        return atid;
    }

    public void setAtid(Long atid) {
        this.atid = atid;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }
}
