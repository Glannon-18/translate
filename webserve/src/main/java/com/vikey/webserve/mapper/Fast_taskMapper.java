package com.vikey.webserve.mapper;

import com.vikey.webserve.entity.Fast_task;
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
public interface Fast_taskMapper extends BaseMapper<Fast_task> {


    List<Fast_task> selectLastFast_task(@Param("uid") Long uid);

    List<Fast_task> selectFast_taskByDate(@Param("uid") Long uid, @Param("name") String name);

    List<Map> getMostFtUseLanguage(@Param("userid") Long id);

    Map<String, LocalDateTime> getLastFtUseLanguage(@Param("userid") Long id);

}
