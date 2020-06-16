package com.vikey.webserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vikey.webserve.Constant;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fast_task implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fast_task;

    private LocalDateTime create_time;

    private String name;

    private String original_text;

    private String translate_text;

    private String original_language;

    private String translate_language;

    private Long uid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFast_task() {
        return fast_task;
    }

    public void setFast_task(String fast_task) {
        this.fast_task = fast_task;
    }

    public LocalDateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDateTime create_time) {
        this.create_time = create_time;
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

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    @JsonGetter("original_language_zh")
    public String getOriginal_language_zh() {
        return Constant.LANGUAGE_ZH.get(getOriginal_language());
    }

    @JsonGetter("translate_language_zh")
    public String getTranslate_language_zh() {
        return Constant.LANGUAGE_ZH.get(getTranslate_language());
    }

    @Override
    public String toString() {
        return "Fast_task{" +
                "id=" + id +
                ", fast_task=" + fast_task +
                ", create_time=" + create_time +
                ", name=" + name +
                ", original_text=" + original_text +
                ", translate_text=" + translate_text +
                ", original_language=" + original_language +
                ", translate_language=" + translate_language +
                ", uid=" + uid +
                "}";
    }
}
