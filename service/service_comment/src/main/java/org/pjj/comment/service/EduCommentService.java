package org.pjj.comment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.comment.entity.EduComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 评论 服务类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-29
 */
public interface EduCommentService extends IService<EduComment> {

    //分页查询课程评论
    Map<String, Object> getCourseComments(Page<EduComment> commentPage, String courseId);
}
