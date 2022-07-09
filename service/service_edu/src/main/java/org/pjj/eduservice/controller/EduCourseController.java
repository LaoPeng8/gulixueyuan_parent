package org.pjj.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import org.pjj.commonutils.R;
import org.pjj.eduservice.client.OssClient;
import org.pjj.eduservice.client.VodClient;
import org.pjj.eduservice.entity.EduCourse;
import org.pjj.eduservice.entity.EduTeacher;
import org.pjj.eduservice.entity.vo.CourseInfoVo;
import org.pjj.eduservice.entity.vo.CoursePublishVo;
import org.pjj.eduservice.entity.vo.CourseQuery;
import org.pjj.eduservice.entity.vo.TeacherQuery;
import org.pjj.eduservice.service.EduCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author pjj
 * @since 2022-03-02
 */
@RestController
@RequestMapping("/eduservice/course")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class EduCourseController {

    @Autowired
    private EduCourseService eduCourseService;

    @Autowired
    private VodClient vodClient;// 通过feign 远程调用 service-vod 中的接口

    @Autowired
    private OssClient ossClient;// 通过feign 远程调用 service-oss 中的接口

    //添加课程基本信息的方法 (返回 添加课程之后的课程id)
    @PostMapping("/addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoVo courseInfoVo) {
        //返回添加之后课程的id, 为了后面的添加大纲使用
        String courseId = eduCourseService.saveCourseInfo(courseInfoVo);
        if(!StringUtils.isEmpty(courseId)){
            return R.ok().data("courseId", courseId);
        }
        return R.ok();
    }

    //根据课程id查询课程基本信息
    @GetMapping("/getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable  String courseId) {
        CourseInfoVo courseInfoVo = eduCourseService.getCourseInfo(courseId);
        return R.ok().data("course", courseInfoVo);
    }

    //修改课程信息
    @PostMapping("/updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo) {
        eduCourseService.updateCourseInfo(courseInfoVo);
        return R.ok();
    }

    //根据课程id查询课程确认信息
    @GetMapping("/getPublishCourseInfo/{courseId}")
    public R getPublishCourseInfo(@PathVariable String courseId) {
        CoursePublishVo coursePublishVo = eduCourseService.publishCourseInfo(courseId);
        if(null != coursePublishVo){
            return R.ok().data("coursePublish", coursePublishVo);
        }
        return R.error();
    }

    //课程的最终发布 (status: Draft 未发布)  (status: Normal 以发布) 将status的值改为Normal表示课程以发布
    @PostMapping("/publishCourse/{id}")
    public R publishCourse(@PathVariable String id) {
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(id);
        eduCourse.setStatus("Normal");
        boolean flag = eduCourseService.updateById(eduCourse);
        if (flag) {
            return R.ok();
        }
        return R.error();
    }

    // 分页查询 课程列表
    @PostMapping("/pageCourseCondition/{current}/{limit}")
    public R pageListCourseCondition(@ApiParam("当前页") @PathVariable Integer current,
                                      @ApiParam("每页数据条数") @PathVariable Integer limit,
                                      @ApiParam("分页查询条件") @RequestBody(required = false) CourseQuery CourseQuery) {

        Page<EduCourse> pageTeacher = new Page<>(current, limit);

        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();//where条件
        //判断条件值是否为空, 如果不为空则拼接条件
        if(!StringUtils.isEmpty(CourseQuery.getTitle())) { // title 不为空
            wrapper.like("title", CourseQuery.getTitle());
        }
        if(!StringUtils.isEmpty(CourseQuery.getStatus())) { // status 不为空
            wrapper.eq("status", CourseQuery.getStatus());
        }
        if(!StringUtils.isEmpty(CourseQuery.getBegin())) { //开始时间 不为空 (有一个问题 前端传的时间格式可能不对, 该怎么判断一下)
            wrapper.ge("gmt_create", CourseQuery.getBegin());// 大于等于 创建时间
        }
        if(!StringUtils.isEmpty(CourseQuery.getEnd())) { //结束时间 不为空
            wrapper.le("gmt_create", CourseQuery.getEnd());  // 小于等于 创建时间
        }

        //根据创建时间排序
        wrapper.orderByDesc("gmt_create");

        eduCourseService.page(pageTeacher, wrapper);//分页查询

        return R.ok().data("total", pageTeacher.getTotal()) //总记录数
                .data("rows", pageTeacher.getRecords()); //数据

    }

    //根据课程id 删除 课程 (包括课程图片) 以及 课程描述(与课程不在同一个表中), 课程下的章节 章节中的图片, 小节, 小节中的视频
    @DeleteMapping("/delete/{courseId}")
    public R deleteCourse(@PathVariable String courseId) {
        // 删除课程封面图片
        EduCourse eduCourse = eduCourseService.getById(courseId);//查询课程, 以便找到课程封面url, 然后删除oss中的封面.png
        if(!StringUtils.isEmpty(eduCourse.getCover())) {
            if(!eduCourse.getCover().equals("https://guli-edu-2022-2-26.oss-cn-beijing.aliyuncs.com/cover.png")) {//不是默认封面才删除封面
                //调用 service_oss服务的接口 删除 课程封面
                R r = ossClient.deleteFile(eduCourse.getCover().replace('/', '-'));
            }
        }

        // 删除 小节视频
        String videoIds = eduCourseService.getVideoIds(courseId);
        if (!StringUtils.isEmpty(videoIds)) {
            R r = vodClient.deleteAliyunVideo(videoIds);//根据视频id 删除阿里云中的视频
        }

        eduCourseService.deleteCourseAndChapterAndVideo(courseId); //删除课程, 课程描述, 章节, 小节

        return R.ok();
    }

    /**
     * 传入课程id, 给课程的购买人数 +1
     * 主要是给 其他服务调用的, 购买课程后, 该课程购买人数需要 +1
     * @param courseId
     * @return
     */
    @PutMapping("/updateCourseBuyCount/{courseId}")
    public R updateCourseBuyCount(@PathVariable String courseId) {

        EduCourse byId = eduCourseService.getById(courseId);

        byId.setBuyCount(byId.getBuyCount() + 1);
        eduCourseService.updateById(byId);

        return R.ok();
    }






}

