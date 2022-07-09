package org.pjj.eduservice.service;

import org.pjj.eduservice.entity.EduSubject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.pjj.eduservice.entity.subject.OneSubject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author pjj
 * @since 2022-02-28
 */
public interface EduSubjectService extends IService<EduSubject> {

    /**
     * 添加课程分类 (根据传入的excel文件)
     * @param file
     */
    void saveSubject(MultipartFile file);

    /**
     * 课程分类列表(树形)
     * @return
     */
    List<OneSubject> getAllOneTwoSubject();
}
