package com.vikey.webserve.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vikey.webserve.entity.Annexe;
import com.vikey.webserve.mapper.AnnexeMapper;
import com.vikey.webserve.service.IAnnexeService;
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
public class AnnexeServiceImpl extends ServiceImpl<AnnexeMapper, Annexe> implements IAnnexeService {


    @Override
    public IPage<Annexe> getAnnexeByPage(Integer currentPage, Integer pageSize, Long atid) {
        Page<Annexe> page = new Page<>(currentPage, pageSize);
        IPage<Annexe> iPage = getBaseMapper().getAnnexeByAtid(page, atid);
        return iPage;
    }
}
