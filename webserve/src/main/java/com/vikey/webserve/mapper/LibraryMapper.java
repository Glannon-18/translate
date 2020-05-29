package com.vikey.webserve.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.entity.Library;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface LibraryMapper extends BaseMapper<Library> {


    Page<Library> list(Page<Library> page, @Param("uid") Long uid, @Param("name") String name);


}
