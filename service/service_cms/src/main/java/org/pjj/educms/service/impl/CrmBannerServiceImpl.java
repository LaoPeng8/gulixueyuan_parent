package org.pjj.educms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.commonutils.R;
import org.pjj.educms.client.OssClient;
import org.pjj.educms.entity.CrmBanner;
import org.pjj.educms.entity.vo.CrmBannerVo;
import org.pjj.educms.mapper.CrmBannerMapper;
import org.pjj.educms.service.CrmBannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务实现类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-17
 */
@Service
public class CrmBannerServiceImpl extends ServiceImpl<CrmBannerMapper, CrmBanner> implements CrmBannerService {


    // 查询 所有 首页 轮播图
    @Cacheable(key = "'selectIndexList'", value = "banner") //返回数据时 加入redis缓存(再次调用时 如redis缓存中有则直接返回)
    @Override
    public List<CrmBanner> selectAllBanner() {

        QueryWrapper<CrmBanner> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("gmt_create");//根据创建时间降序排序

        List<CrmBanner> crmBanners = baseMapper.selectList(wrapper);

        return crmBanners;
    }
}
