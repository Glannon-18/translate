package com.vikey.webserve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vikey.webserve.entity.Annexe;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;

import java.time.LocalDateTime;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface AnnexeMapper extends BaseMapper<Annexe> {

    IPage<Annexe> getAnnexeByAtid(Page<Annexe> page,@Param("atid") Long atid);

    Integer getAnnexeCount(@Param("time") LocalDateTime time, @Param("status") String status);

}
