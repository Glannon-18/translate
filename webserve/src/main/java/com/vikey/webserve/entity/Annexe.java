package com.vikey.webserve.entity;

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
public class Annexe implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private LocalDateTime create_time;

    private String path;

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

    @Override
    public String toString() {
        return "Annexe{" +
            "id=" + id +
            ", create_time=" + create_time +
            ", path=" + path +
        "}";
    }
}
