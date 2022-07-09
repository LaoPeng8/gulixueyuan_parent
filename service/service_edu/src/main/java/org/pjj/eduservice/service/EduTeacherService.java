package org.pjj.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.eduservice.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author pjj
 * @since 2022-02-12
 */
public interface EduTeacherService extends IService<EduTeacher> {

    // 分页查询讲师的方法
    Map<String, Object> getTeacherFrontList(Page<EduTeacher> pageTeacher);
}
