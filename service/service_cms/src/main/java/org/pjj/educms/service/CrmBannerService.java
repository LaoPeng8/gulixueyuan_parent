package org.pjj.educms.service;

import org.pjj.educms.entity.CrmBanner;
import com.baomidou.mybatisplus.extension.service.IService;
import org.pjj.educms.entity.vo.CrmBannerVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-17
 */
public interface CrmBannerService extends IService<CrmBanner> {
    List<CrmBanner> selectAllBanner();
}
