package com.vikey.webserve.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.entity.Library;
import com.vikey.webserve.mapper.LibraryMapper;
import com.vikey.webserve.service.ILibraryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
@Service
public class LibraryServiceImpl extends ServiceImpl<LibraryMapper, Library> implements ILibraryService {

    @Override
    public IPage<Library> list(Page<Library> page, Long uid, String name) {
        return getBaseMapper().list(page, uid, name);
    }
}
