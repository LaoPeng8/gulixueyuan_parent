package org.pjj.comment.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.comment.client.UcenterClient;
import org.pjj.comment.entity.EduComment;
import org.pjj.comment.service.EduCommentService;
import org.pjj.commonutils.JwtUtils;
import org.pjj.commonutils.R;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 评论 前端控制器
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-29
 */
@RestController
@RequestMapping("/educomment/comment")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class EduCommentController {

    @Autowired
    private EduCommentService eduCommentService;

    @Autowired
    private UcenterClient ucenterClient;

    /**
     * 添加评论(发表评论)
     * @param eduComment
     * @return
     */
    @PostMapping("/addComment")
    public R addComment(@RequestBody EduComment eduComment, @RequestHeader("token") String token) {
        if(!JwtUtils.checkToken(token)) {
            new GuliException(20001, "请登录后再评论");
        }
        //id, 创建时间, 修改时间 前端传递过来也不要, mybatis-plus自动填充(其实此处应该使用Vo类的, 根本不接受 这些id啥的参数, 就不用设置了)
        eduComment.setIsDeleted(false);
        eduComment.setId(null);
        eduComment.setGmtCreate(null);
        eduComment.setGmtModified(null);

        //到这里说明 token 有效, 也就是已经登录了
        boolean flag = eduCommentService.save(eduComment);//数据库插入操作
        if(flag) {
            return R.ok();
        }

        return R.error();
    }

    /**
     * 分页查询课程评论 查询某一页的一级评论以及该一页下的 二级评论(二级评论只显示一页(3条评论))
     * @return
     */
    @GetMapping("/getCourseComments/{courseId}/{page}/{limit}")
    public R getCourseComments(@PathVariable String courseId, @PathVariable Integer page, @PathVariable Integer limit) {
        if(limit < 0 || limit > 10) {
            limit = 10;
        }
        if(StringUtils.isEmpty(courseId)) {
            return R.error().message("courseId not is null");
        }

        Page<EduComment> commentPage = new Page<>(page, limit);//将当前页 与 每页几条数据limit封装到分页对象commentPage中

        Map<String, Object> comments = eduCommentService.getCourseComments(commentPage, courseId);

        return R.ok().data(comments);
    }

    /**
     * 二级评论 分页查询的方法
     * @param courseId
     * @param page
     * @return
     */
    @GetMapping("/getCourseComments2/{courseId}/{parentId}/{page}")
    public R getCourseComments2(@PathVariable String courseId, @PathVariable String parentId, @PathVariable Integer page) {
        if(StringUtils.isEmpty(courseId)) {
            return R.error().message("courseId not is null");
        }
        if(StringUtils.isEmpty(parentId)) {
            return R.error().message("parentId not is null");
        }

        Page<EduComment> commentPage = new Page<>(page, 3);//将当前页 与 每页几条数据limit封装到分页对象commentPage中
        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.eq("parent_id", parentId);
        wrapper.orderByDesc("gmt_modified");

        eduCommentService.page(commentPage, wrapper);

        HashMap<String, Object> result = new HashMap<>();
        result.put("items", commentPage.getRecords());//分页列表(返回的二级评论列表)
        result.put("total", commentPage.getTotal());//总记录数(表中一共多少条数据)
        result.put("current", commentPage.getCurrent());//当前页码
        result.put("size", commentPage.getSize());//每页条数
        result.put("pages", commentPage.getPages());//总页数(一共有多少页)
        result.put("hasNext", commentPage.hasNext());// 按当前页码来说 是否有下一页
        result.put("hasPrevious", commentPage.hasPrevious());// 按当前页码来说 是否有上一页

        return R.ok().data(result);
    }

}

