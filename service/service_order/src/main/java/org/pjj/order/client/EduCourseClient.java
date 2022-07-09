package org.pjj.order.client;

import org.pjj.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author PengJiaJun
 * @Date 2022/3/30 23:56
 */
@FeignClient(name = "service-edu")
@Component
public interface EduCourseClient {

    //根据课程id查询课程确认信息
    @GetMapping("/eduservice/course/getPublishCourseInfo/{courseId}")
    R getPublishCourseInfo(@PathVariable("courseId") String courseId);


    //传入课程id, 给课程的购买人数 +1
    @PutMapping("/eduservice/course/updateCourseBuyCount/{courseId}")
    R updateCourseBuyCount(@PathVariable("courseId") String courseId);

}
