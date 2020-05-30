package com.vikey.webserve.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespPageBean {
    private Long total;
    private List<?> data;
    private Long pageSize;

    public RespPageBean(Long total, List<?> data, Long pageSize) {
        this.total = total;
        this.data = data;
        this.pageSize = pageSize;
    }

    public Long getTotal() {

        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
