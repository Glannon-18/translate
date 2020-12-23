package com.vikey.webserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vikey.webserve.Constant;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文档任务对应文档实体类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Annexe implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原文上传时的文件名
     */
    private String name;

    private String status;

    private LocalDateTime create_time;

    /**
     * 原文文件保存相对路径
     */
    private String path;

    private String original_language;

    private String discard;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 译文生成文件相对路径
     */
    private String translate_path;

    @TableField(exist = false)
    private String original_text;

    @TableField(exist = false)
    private String translate_text;

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDateTime create_time) {
        this.create_time = create_time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscard() {
        return discard;
    }

    public void setDiscard(String discard) {
        this.discard = discard;
    }

    @JsonGetter("original_language_zh")
    public String getOriginal_language_zh() {
        return Constant.LANGUAGE_ZH.get(getOriginal_language());
    }

    @JsonGetter("status_zh")
    public String getStatus_zh() {
        return Constant.ANNEXE_STATUS_ZH.get(getStatus());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTranslate_path() {
        return translate_path;
    }

    public void setTranslate_path(String translate_path) {
        this.translate_path = translate_path;
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

    @Override
    public String toString() {
        return "Annexe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", create_time=" + create_time +
                ", path='" + path + '\'' +
                ", original_language='" + original_language + '\'' +
                ", discard='" + discard + '\'' +
                ", type='" + type + '\'' +
                ", translate_path='" + translate_path + '\'' +
                ", original_text='" + original_text + '\'' +
                ", translate_text='" + translate_text + '\'' +
                '}';
    }
}
