package com.vikey.webserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 *     文档任务对应文档实体类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public class Annexe implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文档名称
     */
    private String name;

    private String status;

    private LocalDateTime create_time;

    private String path;

    private Long atid;

    public Long getAtid() {
        return atid;
    }

    public void setAtid(Long atid) {
        this.atid = atid;
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

    @Override
    public String toString() {
        return "Annexe{" +
            "id=" + id +
            ", create_time=" + create_time +
            ", path=" + path +
        "}";
    }
}
