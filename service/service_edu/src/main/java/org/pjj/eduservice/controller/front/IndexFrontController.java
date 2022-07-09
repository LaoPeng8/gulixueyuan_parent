package org.pjj.eduservice.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.commonutils.R;
import org.pjj.eduservice.entity.EduCourse;
import org.pjj.eduservice.entity.EduTeacher;
import org.pjj.eduservice.service.EduCourseService;
import org.pjj.eduservice.service.EduTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台首页
 * @author PengJiaJun
 * @Date 2022/3/18 21:24
 */
@RestController
@RequestMapping("/eduservice/indexfront")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class IndexFrontController {

    @Autowired
    private EduCourseService eduCourseService;

    @Autowired
    private EduTeacherService eduTeacherService;

    // 查询前 8 条热门课程, 查询前 4 位名师
    @GetMapping("/index")
    @Cacheable(value = "index") //缓存
    public R index() {
        // 查询前 8 条热门课程 (按照 view_count 与 buy_count 进行降序, 且 只截取前8条数据)
        QueryWrapper<EduCourse> courseWrapper = new QueryWrapper<>();
        courseWrapper.orderByDesc("view_count", "buy_count");
        courseWrapper.last("limit 8");
        List<EduCourse> courseList = eduCourseService.list(courseWrapper);

        // 查询前 4 位名师 (按照 level 降序 与 gmt_create 升序, 且 只截取前4条数据)
        QueryWrapper<EduTeacher> teacherWrapper = new QueryWrapper<>();
        teacherWrapper.orderByDesc("level");
        teacherWrapper.orderByAsc("gmt_create");
        teacherWrapper.last("limit 4");
        List<EduTeacher> teacherList = eduTeacherService.list(teacherWrapper);


        return R.ok().data("courseList", courseList).data("teacherList", teacherList);
    }

}
