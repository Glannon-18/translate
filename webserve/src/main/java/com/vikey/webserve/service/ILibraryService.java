package com.vikey.webserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.entity.Library;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface ILibraryService extends IService<Library> {

    IPage<Library> list(Page<Library> page, Long uid, String name);

}
