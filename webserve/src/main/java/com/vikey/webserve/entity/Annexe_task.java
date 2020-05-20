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
public class Annexe_task implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String discard;

    private LocalDateTime create_time;

    private String name;

    @TableField(exist = false)
    private String original_text;

    @TableField(exist = false)
    private String translate_text;

    private String original_language;

    private String translate_language;


    @TableField(exist = false)
    private List<Annexe> annexeList;

    private Long uid;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public List<Annexe> getAnnexeList() {
        return annexeList;
    }

    public void setAnnexeList(List<Annexe> annexeList) {
        this.annexeList = annexeList;
    }




    @Override
    public String toString() {
        return "Annexe_task{" +
                "id=" + id +
                ", discard=" + discard +
                ", create_time=" + create_time +
                ", name=" + name +
                ", original_language=" + original_language +
                ", translate_language=" + translate_language +
                ", uid=" + uid +
                "}";
    }
}
