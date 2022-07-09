package org.pjj.eduservice.mapper;

import org.pjj.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.pjj.eduservice.entity.frontvo.CourseWebVo;
import org.pjj.eduservice.entity.vo.CoursePublishVo;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author pjj
 * @since 2022-03-02
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {

    // 课程确认信息
    CoursePublishVo getPublishCourseInfo(String courseId);

    // 查询课程详情 (包括课程详情表中的课程详情, 讲师表中的讲师) (课程表,课程描述表,讲师表)
    CourseWebVo getBaseCourseInfo(String courseId);

}
