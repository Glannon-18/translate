package com.vikey.webserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 * 附件翻译和附件中间表实体类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public class Atask_ann implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long atid;

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

    @Override
    public String toString() {
        return "Atask_ann{" +
                "id=" + id +
                ", atid=" + atid +
                ", aid=" + aid +
                "}";
    }
}
