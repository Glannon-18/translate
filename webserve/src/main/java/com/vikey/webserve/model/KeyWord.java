package com.vikey.webserve.model;

import com.baomidou.mybatisplus.annotation.*;
import com.vikey.webserve.Constant;

import java.io.Serializable;
import java.util.Date;

/**
 * 关键词实体类
 */

@TableName("keyword")
public class KeyWord implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "discard")
    @TableLogic(value = Constant.NOT_DELETE, delval = Constant.DELETE)
    private String discard;

    @TableField("create_time")
    private Date createTime;

    @TableField("word")
    private String word;

    @TableField("lid")
    private Long lid;//词库id

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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getLid() {
        return lid;
    }

    public void setLid(Long lid) {
        this.lid = lid;
    }
}
