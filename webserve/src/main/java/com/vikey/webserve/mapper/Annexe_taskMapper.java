package com.vikey.webserve.mapper;

import com.vikey.webserve.entity.Annexe_task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wkw
 * @since 2020-05-18
 */
public interface Annexe_taskMapper extends BaseMapper<Annexe_task> {

    List<Annexe_task> getAnnexe_taskByDate(@Param("uid") Long uid, @Param("name") String name);

    Map getAllTaskCount(@Param("userid") Long id);

    List<Map> getMostAtUseLanguage(@Param("userid") Long id);

    Map<String, Object> getLastAtUseLanguage(@Param("userid") Long id);
}
