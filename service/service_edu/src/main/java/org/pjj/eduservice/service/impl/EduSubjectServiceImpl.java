package org.pjj.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.eduservice.entity.EduSubject;
import org.pjj.eduservice.entity.excel.SubjectExcel;
import org.pjj.eduservice.entity.subject.OneSubject;
import org.pjj.eduservice.entity.subject.TwoSubject;
import org.pjj.eduservice.listener.SubjectExcelListener;
import org.pjj.eduservice.mapper.EduSubjectMapper;
import org.pjj.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author pjj
 * @since 2022-02-28
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Autowired
    private EduSubjectMapper subjectMapper;

    /**
     * 添加课程分类 (根据传入的excel文件)
     * @param file
     */
    @Override
    public void saveSubject(MultipartFile file) {
        try{
            InputStream in = file.getInputStream();//获取用户上传文件的输入流
            EasyExcel.read(in, SubjectExcel.class, new SubjectExcelListener(this)).sheet().doRead();//调用方法进行读取
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询 所有分类 返回 各一级分类 以及其 二级分类(子分类)
     *
     * @return
     */
    @Override
    public List<OneSubject> getAllOneTwoSubject() {

        List<OneSubject> oneSubjectList = new ArrayList<>();//需要返回前端的一级分类集合(VO类的集合)

        QueryWrapper<EduSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", "0");
        List<EduSubject> list = subjectMapper.selectList(queryWrapper);//查询一级分类


        for(EduSubject tempOneSubject : list){//遍历数据库的一级分类, 并封装为前端需要的vo类数据
            QueryWrapper<EduSubject> queryWrapperTwo = new QueryWrapper<>();
            queryWrapperTwo.eq("parent_id", tempOneSubject.getId());
            List<EduSubject> listTwo = subjectMapper.selectList(queryWrapperTwo);//查询各一级分类的二级分类

            List<TwoSubject> twoSubjectList = new ArrayList<>();//需要返回前端的二级分类集合(VO类的集合)
            for(EduSubject temp : listTwo){//遍历数据库的二级分类, 并封装为前端需要的vo类数据
                TwoSubject twoSubject = new TwoSubject();
                twoSubject.setId(temp.getId());
                twoSubject.setTitle(temp.getTitle());
                twoSubjectList.add(twoSubject);//每个一级分类都有N个二级分类, 所以使用集合存储每个一级分类的二级分类
            }

            OneSubject oneSubject = new OneSubject();
            oneSubject.setId(tempOneSubject.getId());
            oneSubject.setTitle(tempOneSubject.getTitle());
            oneSubject.setChildren(twoSubjectList);
            oneSubjectList.add(oneSubject);//一级分类的集合
        }

        return oneSubjectList;
    }

}
