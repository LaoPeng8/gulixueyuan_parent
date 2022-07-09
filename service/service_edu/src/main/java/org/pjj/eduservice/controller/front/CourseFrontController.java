package org.pjj.eduservice.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.commonutils.R;
import org.pjj.eduservice.entity.EduChapter;
import org.pjj.eduservice.entity.EduCourse;
import org.pjj.eduservice.entity.chapter.ChapterVo;
import org.pjj.eduservice.entity.frontvo.CourseFrontVo;
import org.pjj.eduservice.entity.frontvo.CourseWebVo;
import org.pjj.eduservice.service.EduChapterService;
import org.pjj.eduservice.service.EduCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author PengJiaJun
 * @Date 2022/3/27 13:09
 */
@RequestMapping("/eduservice/coursefront")
@RestController
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class CourseFrontController {

    @Autowired
    private EduCourseService eduCourseService;

    @Autowired
    private EduChapterService eduChapterService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //条件查询带分页查询课程
    @PostMapping("/getFrontCourseList/{page}/{limit}")
    public R getFrontCourseList(@PathVariable Integer page, @PathVariable Integer limit, @RequestBody(required = false) CourseFrontVo courseFrontVo) {
        if(limit < 0 || limit > 8) {
            limit = 8;
        }
        Page<EduCourse> pageCourse = new Page<>(page, limit);//将当前页 与 每页几条数据limit封装到分页对象pageCourse中

        Map<String, Object> result = eduCourseService.getCourseFrontList(pageCourse, courseFrontVo);


        return R.ok().data(result);
    }

    // 查询课程详情
    @GetMapping("/getFrontCourseInfo/{courseId}")
    public R getFrontCourseInfo(@PathVariable String courseId, HttpServletRequest request) {

        //根据课程id, 查询课程信息(包括课程详情表中的课程详情, 讲师表中的讲师)
        CourseWebVo courseWebVo = eduCourseService.getBaseCourseInfo(courseId);

        //根据课程id查询章节和小节
        List<ChapterVo> chapterVideo =eduChapterService.getChapterVideoByCourseId(courseId);


        // 课程浏览数 + 1, 规则: 同一ip下 访问某个课程 该课程浏览数 + 1, 但是10分钟内只能 +1 一次
        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();
        String ipValue = redisTemplate.opsForValue().get(ip+":"+courseId);
        if(StringUtils.isEmpty(ipValue)) {//如果从redis下取出key为该ip+courseId的value 取不到, 说明该ip下10分钟内没有访问过该课程, 则让该课程浏览数 +1
            EduCourse byId = eduCourseService.getById(courseId);
            byId.setViewCount(byId.getViewCount() + 1);
            eduCourseService.updateById(byId);

            redisTemplate.opsForValue().set(ip+":"+courseId, "1", 10, TimeUnit.MINUTES);//将访问该接口的key=ip+courseId与value=1加入redis 过期时间 10 分钟
        }
        //如果取出了key为该ip+courseId的value, 则说明该ip10分钟内已经访问过该课程了, 则啥也不干(课程浏览数 不加1)


        return R.ok().data("courseInfo", courseWebVo).data("chapterVideoList", chapterVideo);
    }

}
