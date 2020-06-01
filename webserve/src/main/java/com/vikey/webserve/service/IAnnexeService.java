package com.vikey.webserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.entity.Annexe;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface IAnnexeService extends IService<Annexe> {

    /**
     * 文本翻译附件列表翻页
     *
     * @param currentPage
     * @param pageSize
     * @param atid
     * @return
     */
    IPage<Annexe> getAnnexeByPage(Integer currentPage, Integer pageSize, Long atid);


    Integer getAnnexeCount(LocalDateTime time, String status);

}
