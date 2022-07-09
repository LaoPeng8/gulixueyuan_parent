package org.pjj.educms.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.commonutils.R;
import org.pjj.educms.client.OssClient;
import org.pjj.educms.entity.CrmBanner;
import org.pjj.educms.entity.vo.CrmBannerVo;
import org.pjj.educms.service.CrmBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 首页轮播图 (后台)
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-17
 */
@RestController
@RequestMapping("/educms/banneradmin")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class AdminBannerController {

    @Autowired
    private CrmBannerService bannerService;

    @Autowired
    private OssClient ossClient; // 通过 feign 远程调用 service-oss 的服务

    /**
     * 查询 所有 首页 轮播图
     * @return
     */
    @GetMapping("/getAllBanner")
    public R getAllBanner() {

        QueryWrapper<CrmBanner> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("gmt_create");//根据创建时间降序排序

        List<CrmBanner> list = bannerService.list(wrapper);
        if (!list.isEmpty()) {
            return R.ok().data("list", list);
        }

        return R.error();
    }

    /**
     * 增加 首页 轮播图
     *
     * @return
     */
    @PostMapping("/addBanner")
    public R addBanner(@RequestBody CrmBannerVo crmBannerVo) {

        int count = bannerService.count(null);
        if(count >= 5){
            return R.error().message("首页轮播图已达到最大数: 5, 请删除后再添加");
        }

        CrmBanner crmBanner = new CrmBanner();
        crmBanner.setLinkUrl(crmBannerVo.getLinkUrl());
        crmBanner.setTitle(crmBannerVo.getTitle());
        crmBanner.setImageUrl(crmBannerVo.getImageUrl());

        boolean flag = bannerService.save(crmBanner);

        if (flag) {
            return R.ok();
        }

        return R.error();
    }

    @DeleteMapping("/delBanner/{id}")
    public R delBanner(@PathVariable String id) {

        // 调用 service_oss 服务 删除图片 然后再删除数据库记录
        CrmBanner crmBanner = bannerService.getById(id);
        if (!StringUtils.isEmpty(crmBanner.getImageUrl())) {
            ossClient.deleteFile(crmBanner.getImageUrl().replace("/", "-"));//删除图片
        }

        boolean flag = bannerService.removeById(id);//删除 数据库表记录
        if (flag) {
            return R.ok();
        }

        return R.error();
    }

}

