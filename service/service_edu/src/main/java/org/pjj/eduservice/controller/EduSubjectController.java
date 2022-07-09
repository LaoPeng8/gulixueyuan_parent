package org.pjj.eduservice.controller;


import org.pjj.commonutils.R;
import org.pjj.eduservice.entity.subject.OneSubject;
import org.pjj.eduservice.service.EduSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author pjj
 * @since 2022-02-28
 */
@RestController
@RequestMapping("/eduservice/subject")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class EduSubjectController {

    @Autowired
    private EduSubjectService eduSubjectService;

    /**
     * 添加课程分类 (根据上传的excel)
     * @param file excel文件
     * @return
     */
    @PostMapping("/addSubject")
    public R addSubject(MultipartFile file) {

//        System.out.println(file.isEmpty());//判空
//        System.out.println(file.getSize());//单位是字节
//        System.out.println(file.getContentType());//获取的是文件的类型(不是后缀名)

        //application/vnd.ms-excel为后缀 .xls
        //application/vnd.openxmlformats-officedocument.spreadsheetml.sheet 为后缀 .xlsx
        //只有文件类型为excel 才会上传文件 同时 文件大小 小于 5000 同时 文件不为空, 否则 啥也不执行
        if ((file.getContentType().equals("application/vnd.ms-excel") || file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                && file.getSize() <= 50000L && !file.isEmpty()){

            eduSubjectService.saveSubject(file);
        }

        return R.ok();
    }

    /**
     * 课程分类列表(树形)
     */
    @GetMapping("/getAllSubject")
    public R getAllSubject() {
        List<OneSubject> allOneTwoSubject = eduSubjectService.getAllOneTwoSubject();
        return R.ok().data("list", allOneTwoSubject);
    }


}

