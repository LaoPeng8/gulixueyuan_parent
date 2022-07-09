package org.pjj.eduservice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pjj.eduservice.entity.EduSubject;
import org.pjj.eduservice.entity.subject.OneSubject;
import org.pjj.eduservice.service.EduSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.List;

/**
 * @author PengJiaJun
 * @Date 2022/3/1 16:08
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {

    @Autowired
    EduSubjectService subjectService;


    /**
     * 测试 查询 所有分类 返回 各一级分类 以及其 二级分类(子分类)
     *
     * 脚本语言
     * [Python, shell]
     * 前端
     * [HTML5, CSS, JavaScript]
     * 人工智能
     * [Python]
     * 数据库
     * [MySQL, Oracle, SqlServer]
     * 大数据
     * [Python]
     */
    @Test
    public void test(){
        QueryWrapper<EduSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", "0");
        List<EduSubject> list = subjectService.list(queryWrapper);//查询一级分类
//        System.out.println(list);

        HashMap<String, List<String>> map = new HashMap();
        for(EduSubject oneSubject : list){
            QueryWrapper<EduSubject> queryWrapperTwo = new QueryWrapper<>();
            queryWrapperTwo.eq("parent_id", oneSubject.getId());
            List<EduSubject> listTwo = subjectService.list(queryWrapperTwo);//查询各一级分类的二级分类
//            System.out.println(listTwo);


            ArrayList<String> objects = new ArrayList<>();
            for(EduSubject temp : listTwo){
                objects.add(temp.getTitle());
            }
            map.put(oneSubject.getTitle(), objects);
        }

        System.out.println("=============================");
        for (String temp : map.keySet()) {
            System.out.println(temp);
            System.out.println(map.get(temp));
        }

    }

    @Test
    public void test02() {
        List<OneSubject> allOneTwoSubject = subjectService.getAllOneTwoSubject();
        for(OneSubject temp : allOneTwoSubject){
            System.out.println(temp);
        }
    }

    @Test
    public void test03() {
        String filePath = "https://guli-edu-2022-2-26.oss-cn-beijing.aliyuncs.com/2022/02/27/10/4ce8d93e4d7940e3a831eadb89ef0045file.png";
        String str = filePath.substring(55);
        System.out.println(str);
    }

    @Test
    public void test04() {
        StringBuffer sb = new StringBuffer();
        System.out.println(sb.length());// 0

        sb.append("hhh");

        System.out.println(sb.length());// 3
    }

}
