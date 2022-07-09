package org.pjj.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.pjj.eduservice.entity.frontvo.CourseFrontVo;
import org.pjj.eduservice.entity.frontvo.CourseWebVo;
import org.pjj.eduservice.entity.vo.CourseInfoVo;
import org.pjj.eduservice.entity.vo.CoursePublishVo;

import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author pjj
 * @since 2022-03-02
 */
public interface EduCourseService extends IService<EduCourse> {

    // 添加课程基本信息的方法 (返回添加课程的 id )
    String saveCourseInfo(CourseInfoVo courseInfoVo);

    // 查询课程信息
    CourseInfoVo getCourseInfo(String courseId);

    //修改课程信息
    void updateCourseInfo(CourseInfoVo courseInfoVo);

    // 返回课程确认信息
    CoursePublishVo publishCourseInfo(String courseId);

    // 根据课程id 删除 课程 (包括课程图片) 以及 课程描述(与课程不在同一个表中), 课程下的章节 章节中的图片, 小节, 小节中的视频
    boolean deleteCourseAndChapterAndVideo(String courseId);

    // 根据 课程id 查询出 所有小节的视频id 并封装为 "id1, id2, id3, ..." 这种形式返回 (最后由 controller中调用 service_vod 中的接口删除视频)
    String getVideoIds(String courseId);

    // 条件查询 带分页查询课程
    Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFrontVo courseFrontVo);

    // 查询课程详情 (包括课程详情表中的课程详情, 讲师表中的讲师) (课程表,课程描述表,讲师表)
    CourseWebVo getBaseCourseInfo(String courseId);
}
