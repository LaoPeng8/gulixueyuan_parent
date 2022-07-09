package org.pjj.eduservice.controller;


import org.pjj.commonutils.R;
import org.pjj.eduservice.entity.EduChapter;
import org.pjj.eduservice.entity.chapter.ChapterVo;
import org.pjj.eduservice.entity.chapter.VideoVo;
import org.pjj.eduservice.service.EduChapterService;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author pjj
 * @since 2022-03-02
 */
@RestController
@RequestMapping("/eduservice/chapter")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class EduChapterController {

    @Autowired
    private EduChapterService eduChapterService;

    //课程大纲列表, 根据课程id查询
    @GetMapping("/getChapterVideo/{courseId}")
    public R getChapterVideo(@PathVariable String courseId) {
        List<ChapterVo> list = eduChapterService.getChapterVideoByCourseId(courseId);

        if(list == null){
            return R.error().message("返回集合为空");
        }

        return R.ok().data("allChapterVideo", list);
    }

    //添加章节
    @PostMapping("/addChapter")
    public R addChapter(@RequestBody EduChapter eduChapter) {
        eduChapterService.save(eduChapter);
        return R.ok();
    }

    //根据章节id查询
    @GetMapping("/getChapterInfo/{chapterId}")
    public R getChapterInfo(@PathVariable String chapterId) {
        EduChapter eduChapter = eduChapterService.getById(chapterId);
        return R.ok().data("chapter", eduChapter);
    }

    //修改章节
    @PutMapping("/updateChapter")
    public R updateChapter(@RequestBody EduChapter eduChapter) {
        boolean flag = eduChapterService.updateById(eduChapter);
        if(flag) {
            return R.ok();
        }
        return R.error();
    }

    //删除章节
    @DeleteMapping("/deleteChapter/{chapterId}")
    public R deleteChapter(@PathVariable String chapterId) {
        boolean flag = false;
        try{
            flag = eduChapterService.deleteChapter(chapterId);
        } catch (GuliException e) {
            return R.warn().message("该章节的小结不为空, 不能删除该章节");
        }

        if(flag) {
            return R.ok();
        }

        return R.error();
    }

}

