package org.pjj.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.pjj.eduservice.entity.EduTeacher;
import org.pjj.eduservice.mapper.EduTeacherMapper;
import org.pjj.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author pjj
 * @since 2022-02-12
 */
@Service
public class EduTeacherServiceImpl extends ServiceImpl<EduTeacherMapper, EduTeacher> implements EduTeacherService {


    

    /**
     * 分页查询讲师的方法
     *
     * @param pageTeacher 传入的 pageTeacher 中包含了 当前页page 以及 每页条数limit
     * @return
     */
    @Override
    public Map<String, Object> getTeacherFrontList(Page<EduTeacher> pageTeacher) {

        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("gmt_create");//升序

        baseMapper.selectPage(pageTeacher, wrapper);//查询后 会将分页结果封装到 pageTeacher对象中(通过该对象获取数据)

        List<EduTeacher> records = pageTeacher.getRecords();//分页数据
        long current = pageTeacher.getCurrent();//当前页码
        long pages = pageTeacher.getPages();//总页数(一共有多少页)
        long size = pageTeacher.getSize();//每页条数
        long total = pageTeacher.getTotal();//总记录数(表中一共多少条数据)
        boolean hasNext = pageTeacher.hasNext();// 按当前页码来说 是否有下一页
        boolean hasPrevious = pageTeacher.hasPrevious();// 按当前页码来说 是否有上一页

        Map<String, Object> result = new HashMap<>();
        result.put("items", records);
        result.put("current", current);
        result.put("pages", pages);
        result.put("size", size);
        result.put("total", total);
        result.put("hasNext", hasNext);
        result.put("hasPrevious", hasPrevious);

        return result;//感觉直接返回 pageTeacher 也可以, 没有太大必要封装为map返回
    }

}
