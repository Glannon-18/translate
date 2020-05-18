package com.vikey.webserve.model;

import com.baomidou.mybatisplus.annotation.*;
import com.vikey.webserve.Constant;

import java.util.Date;
import java.util.List;

/**
 * 文本翻译任务
 */
@TableName("annexe_task")
public class AnnexeTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "discard")
    @TableLogic(value = Constant.NOT_DELETE, delval = Constant.DELETE)
    private String discard;//逻辑删除标识

    @TableField("create_time")
    private Date createTime;

    @TableField("name")
    private String name;

    @TableField(exist = false)
    private String original_text;

    @TableField(exist = false)
    private String translate_text;

    @TableField("original_language")
    private String original_language;

    @TableField("translate_language")
    private String translate_language;

    @TableField(exist = false)
    private List<Annexe> annexeList;

    @TableField(value = "uid")
    private Long uid;//用户id

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

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

    public String getOriginal_text() {
        return original_text;
    }

    public void setOriginal_text(String original_text) {
        this.original_text = original_text;
    }

    public String getTranslate_text() {
        return translate_text;
    }

    public void setTranslate_text(String translate_text) {
        this.translate_text = translate_text;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getTranslate_language() {
        return translate_language;
    }

    public void setTranslate_language(String translate_language) {
        this.translate_language = translate_language;
    }

    public List<Annexe> getAnnexeList() {
        return annexeList;
    }

    public void setAnnexeList(List<Annexe> annexeList) {
        this.annexeList = annexeList;
    }
}
