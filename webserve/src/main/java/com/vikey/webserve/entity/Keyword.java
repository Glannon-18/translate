package com.vikey.webserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public class Keyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String discard;

    private LocalDateTime create_time;

    private String word;

    private Long lid;

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

    @Override
    public String toString() {
        return "Keyword{" +
                "id=" + id +
                ", discard=" + discard +
                ", create_time=" + create_time +
                ", word=" + word +
                ", lid=" + lid +
                "}";
    }
}
