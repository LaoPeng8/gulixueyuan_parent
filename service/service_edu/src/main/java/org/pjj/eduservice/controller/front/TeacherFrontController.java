package org.pjj.eduservice.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.commonutils.R;
import org.pjj.eduservice.entity.EduCourse;
import org.pjj.eduservice.entity.EduTeacher;
import org.pjj.eduservice.entity.frontvo.TeacherVo;
import org.pjj.eduservice.service.EduCourseService;
import org.pjj.eduservice.service.EduTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author PengJiaJun
 * @Date 2022/3/25 20:08
 */
@RestController
@RequestMapping("/eduservice/teacherfront")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class TeacherFrontController {

    @Autowired
    private EduTeacherService eduTeacherService;

    @Autowired
    private EduCourseService eduCourseService;

    /**
     * 分页查询讲师的方法
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("/getTeacherFrontList/{page}/{limit}")
    public R getTeacherFrontList(@PathVariable Integer page, @PathVariable Integer limit){
        if(limit < 0 || limit > 8) {
            limit = 8;
        }

        Page<EduTeacher> pageTeacher = new Page<>(page, limit);

        Map<String, Object> map =  eduTeacherService.getTeacherFrontList(pageTeacher);

        return R.ok().data(map);
    }

    /**
     * 根据id查询讲师
     * @param id
     * @return
     */
    @GetMapping("/getTeacher/{id}")
    public R getTeacher(@PathVariable String id) {

        // 查询讲师基本信息
        EduTeacher byId = eduTeacherService.getById(id);
        TeacherVo teacher = new TeacherVo();
        BeanUtils.copyProperties(byId, teacher);

        // 根据讲师id查询讲师所讲课程
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", id);
        wrapper.last("limit 4");
        List<EduCourse> courseList = eduCourseService.list(wrapper);

        return R.ok().data("teacher", teacher).data("courseList", courseList);
    }

    

}
