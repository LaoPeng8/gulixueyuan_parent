package org.pjj.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.eduservice.entity.EduChapter;
import org.pjj.eduservice.entity.EduCourse;
import org.pjj.eduservice.entity.EduCourseDescription;
import org.pjj.eduservice.entity.EduVideo;
import org.pjj.eduservice.entity.frontvo.CourseFrontVo;
import org.pjj.eduservice.entity.frontvo.CourseWebVo;
import org.pjj.eduservice.entity.vo.CourseInfoVo;
import org.pjj.eduservice.entity.vo.CoursePublishVo;
import org.pjj.eduservice.mapper.EduCourseMapper;
import org.pjj.eduservice.service.EduChapterService;
import org.pjj.eduservice.service.EduCourseDescriptionService;
import org.pjj.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pjj.eduservice.service.EduVideoService;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author pjj
 * @since 2022-03-02
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService eduCourseDescriptionService;

    @Autowired
    private EduVideoService eduVideoService;

    @Autowired
    private EduChapterService eduChapterService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加课程基本信息的方法
     *
     * 1. 向课程表(edu_course) 添加 课程基本信息
     * 2. 向课程简介表(edu_course_description) 添加 课程简介信息
     * @param courseInfoVo
     * @return 返回添加课程的 id
     */
    @Override
    @Transactional
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
        courseInfoVo.setId(null);//前端传入id也不接收

        // 1. 向课程表(edu_course) 添加 课程基本信息
        EduCourse eduCourse = new EduCourse();
        eduCourse.setSubjectParentId("0");// 防止报错, edu_course表该字段不能为null, 业务暂时 也不到该字段, 先默认为0, 测试使用
        BeanUtils.copyProperties(courseInfoVo, eduCourse);// CourseInfoVo对象 转为 eduCourse对象
        int insert = baseMapper.insert(eduCourse);//插入表 edu_course (baseMapper是父类提供的)
        if (insert <= 0) {
            //添加失败
            throw new GuliException(20001, "添加课程信息失败");
        }

        // 2. 向课程简介表(edu_course_description) 添加 课程简介信息
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(eduCourse.getId());//插入id (课程简介表的id 就是 课程表的id)(课程表的id是自增的)
        courseDescription.setDescription(courseInfoVo.getDescription());//插入课程简介

        boolean save = eduCourseDescriptionService.save(courseDescription);//插入表 edu_course_description
        if (save == false){
            //添加失败
            throw new GuliException(20001, "添加课程信息失败");
        }

        return eduCourse.getId();// 返回添加课程的 id
    }

    @Override
    public CourseInfoVo getCourseInfo(String courseId) {

        //查询 课程基本信息
        EduCourse eduCourse = baseMapper.selectById(courseId);//baseMapper是父类提供的

        //查询 课程描述信息
        EduCourseDescription eduCourseDescription = eduCourseDescriptionService.getById(courseId);

        CourseInfoVo result = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse, result);
        result.setDescription(eduCourseDescription.getDescription());

        return result;
    }

    //修改课程信息
    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {

        // 1. 修改课程基本信息表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);

        int update = baseMapper.updateById(eduCourse);//修改
        if(update == 0){
            throw new GuliException(20000, "修改课程信息失败");
        }

        //2. 修改课程描述表
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseInfoVo.getId());
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        boolean update2 = eduCourseDescriptionService.updateById(eduCourseDescription);//修改
        if(!update2){
            throw new GuliException(20000, "修改课程描述信息失败");
        }

    }

    // 根据课程id查询课程确认信息
    @Override
    public CoursePublishVo publishCourseInfo(String courseId) {
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(courseId);
        return publishCourseInfo;
    }

    // 根据课程id 删除 课程 (包括课程图片) 以及 课程描述(与课程不在同一个表中), 课程下的章节, 小节
    // 小节 中的 视频(此处不做删除)  由getVideoIds(courseId) 找出课程的所有视频id (最后由 controller中调用 service_vod 中的接口删除视频)
    @Override
    public boolean deleteCourseAndChapterAndVideo(String courseId) {

        //删除章节表下的章节
        QueryWrapper<EduChapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("course_id", courseId);
        eduChapterService.remove(chapterWrapper);

        //删除小节表中的小节
        QueryWrapper<EduVideo> videoWrapper = new QueryWrapper<>();
        videoWrapper.eq("course_id", courseId);
        eduVideoService.remove(videoWrapper);

        //删除课程
        baseMapper.deleteById(courseId);

        //删除课程描述表中的 课程描述
        eduCourseDescriptionService.removeById(courseId);

        return true;
    }

    // 根据 课程id 查询出 所有小节的视频id 并封装为 "id1, id2, id3, ..." 这种形式返回 (最后由 controller中调用 service_vod 中的接口删除视频)
    public String getVideoIds(String courseId) {
        StringBuffer result = new StringBuffer();
        QueryWrapper<EduVideo> videoFileWrapper = new QueryWrapper<>();
        videoFileWrapper.select("video_source_id");
        videoFileWrapper.eq("course_id", courseId);
        List<EduVideo> list = eduVideoService.list(videoFileWrapper);//根据 课程id 查询出该课程所有的小节
        for(EduVideo eduVideo : list){
            if(!StringUtils.isEmpty(eduVideo.getVideoSourceId())) { //如果 小节的视频id不为空, 则加入需要返回的字符串中
                result.append(eduVideo.getVideoSourceId());
                result.append(",");
            }
        }
        // result 如上方for循环 最后肯定为多出一个 "," 所以需要截取字符串 不保存最后一个字符
        if(result.length() <= 0) {
            // 如果 result 长度为 0 说明一个小节视频都没有(一个视频id都没有) 那么就没有经过上面的for 就不会多出一个 , 就不用截取
            // 否则 长度为 0 还 -1 截取 就会报下标越界 String index out of range: -1
            return "";
        }

        return result.toString().substring(0, result.length() - 1);
    }

    /**
     * 条件查询 带分页查询课程
     * @param pageCourse 分页对象
     * @param courseFrontVo 条件对象
     * @return
     */
    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFrontVo courseFrontVo) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "Normal");//只查询发布了的课程

        if(!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())) {
            //如果传入的一级分类id courseFrontVo.getSubjectParentId() 不为空, 则按照 一级分类id查询
            wrapper.eq("subject_parent_id", courseFrontVo.getSubjectParentId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectId())) {
            //如果传入的二级分类id courseFrontVo.getSubjectId() 不为空, 则按照 二级分类id查询
            wrapper.eq("subject_id", courseFrontVo.getSubjectId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())) {
            //如果传入的 按销量排序不为空则进行排序(降序) (即销量最高的显示在最前面)
            wrapper.orderByDesc("buy_count");
        }
        if(!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())) {
            //如果传入的 “最新” 不为空 则按最新排序 (降序) (即创建时间最小的最前面)
            wrapper.orderByDesc("gmt_create");
        }
        if(!StringUtils.isEmpty(courseFrontVo.getPriceSort())) {
            //如果传入的 价格 不为空 则按价格进行排序(降序) (即最贵的在最前面)
            wrapper.orderByDesc("price");
        }

        baseMapper.selectPage(pageCourse, wrapper);//查询后,mybatis-plus会将数据封装到 pageCourse 中

        //将数据封装到map中返回
        List<EduCourse> records = pageCourse.getRecords();//分页数据
        long total = pageCourse.getTotal();//总记录数(表中一共多少条数据)
        long current = pageCourse.getCurrent();//当前页码
        long size = pageCourse.getSize();//每页条数
        long pages = pageCourse.getPages();//总页数(一共有多少页)
        boolean hasNext = pageCourse.hasNext();// 按当前页码来说 是否有下一页
        boolean hasPrevious = pageCourse.hasPrevious();// 按当前页码来说 是否有上一页

        HashMap<String, Object> result = new HashMap<>();
        result.put("items", records);
        result.put("total", total);
        result.put("current", current);
        result.put("size", size);
        result.put("pages", pages);
        result.put("hasNext", hasNext);
        result.put("hasPrevious", hasPrevious);

        return result;
    }

    // 查询课程详情 (包括课程详情表中的课程详情, 讲师表中的讲师) (课程表,课程描述表,讲师表)
    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {

        CourseWebVo baseCourseInfo = baseMapper.getBaseCourseInfo(courseId);

        return baseCourseInfo;
    }
}
