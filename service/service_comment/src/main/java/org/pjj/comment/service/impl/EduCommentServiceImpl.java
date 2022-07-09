package org.pjj.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.comment.entity.EduComment;
import org.pjj.comment.entity.vo.CommentVo;
import org.pjj.comment.mapper.EduCommentMapper;
import org.pjj.comment.service.EduCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-29
 */
@Service
public class EduCommentServiceImpl extends ServiceImpl<EduCommentMapper, EduComment> implements EduCommentService {

    //分页查询课程评论
    @Override
    public Map<String, Object> getCourseComments(Page<EduComment> commentOnePage, String courseId) {
        List<CommentVo> commentOneList = new ArrayList<>();//一级评论列表

        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);//查询某个课程的评论
        wrapper.eq("parent_id", 0);//只查询一级评论
        wrapper.orderByDesc("gmt_modified");//按时间降序排序 (即 最新发布的评论在最前面)

        baseMapper.selectPage(commentOnePage, wrapper);//分页查询

        for(EduComment commentOne : commentOnePage.getRecords()) {//遍历一级评论
            Page<EduComment> commentTwoPage = new Page<>(1, 3);//二级评论分页对象

            QueryWrapper<EduComment> wrapper2 = new QueryWrapper<>();
            wrapper2.eq("course_id", courseId);//查询某给课程的评论
            wrapper2.eq("parent_id", commentOne.getId());//根据一级评论 查询对应的 二级评论
            wrapper2.orderByDesc("gmt_modified");

            baseMapper.selectPage(commentTwoPage, wrapper2);//分页查询二级评论

            //一级评论对象
            CommentVo commentOneVo = new CommentVo();
            BeanUtils.copyProperties(commentOne, commentOneVo);

            //二级评论数据
            HashMap<String, Object> commentTwoMap = new HashMap<>();
            commentTwoMap.put("items", commentTwoPage.getRecords());//二级评论列表
            commentTwoMap.put("total", commentTwoPage.getTotal());//二级评论 总记录数(表中一共多少条数据)
            commentTwoMap.put("current", commentTwoPage.getCurrent());//二级评论 当前页码
            commentTwoMap.put("size", commentTwoPage.getSize());//二级评论 每页条数
            commentTwoMap.put("pages", commentTwoPage.getPages());//二级评论 总页数(一共有多少页)
            commentTwoMap.put("hasNext", commentTwoPage.hasNext());//二级评论 按当前页码来说 是否有下一页
            commentTwoMap.put("hasPrevious", commentTwoPage.hasPrevious());//二级评论 按当前页码来说 是否有上一页

            commentOneVo.setCommentTwo(commentTwoMap);//给一级评论 设置对应的 二级评论

            commentOneList.add(commentOneVo);//将一级评论放入集合 最终返回

        }

        // 返回一级评论列表 以及 一级评论的分页数据
        HashMap<String, Object> result = new HashMap<>();
        result.put("items", commentOneList);//带有二级评论的一级评论列表
        result.put("total", commentOnePage.getTotal());//总记录数(表中一共多少条数据)
        result.put("current", commentOnePage.getCurrent());//当前页码
        result.put("size", commentOnePage.getSize());//每页条数
        result.put("pages", commentOnePage.getPages());//总页数(一共有多少页)
        result.put("hasNext", commentOnePage.hasNext());// 按当前页码来说 是否有下一页
        result.put("hasPrevious", commentOnePage.hasPrevious());// 按当前页码来说 是否有上一页

        return result;
    }
}
