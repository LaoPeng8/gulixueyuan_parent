package org.pjj.educms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.commonutils.R;
import org.pjj.educms.entity.CrmBanner;
import org.pjj.educms.service.CrmBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author PengJiaJun
 * @Date 2022/3/17 22:20
 */
@RestController
@RequestMapping("/educms/bannerfront")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class FrontBannerController {

    @Autowired
    private CrmBannerService bannerService;

    /**
     * 查询 所有 首页 轮播图
     * @return
     */
    @GetMapping("/getAllBanner")
    public R getAllBanner() {

        List<CrmBanner> list = bannerService.selectAllBanner();

        if (!list.isEmpty()) {
            return R.ok().data("list", list);
        }

        return R.error();
    }

}
