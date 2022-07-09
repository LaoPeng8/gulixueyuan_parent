package org.pjj.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.eduservice.entity.EduSubject;
import org.pjj.eduservice.entity.excel.SubjectExcel;
import org.pjj.eduservice.service.EduSubjectService;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;

/**
 * EasyExcel 读取excel文件 所需要的 监听器Listener
 * @author PengJiaJun
 * @Date 2022/2/28 22:41
 */
public class SubjectExcelListener extends AnalysisEventListener<SubjectExcel> {

    //该类没有加入SpringIOC 所以不能使用 @Autowired (至于为什么不直接将该类使用 @Component 加入IOC管理 这样就可以使用@Autowired)
    //所以需要通过构造方法传入 (需要该service实现读取的excel写入数据库表)
    public EduSubjectService eduSubjectService;

    public SubjectExcelListener() {
    }
    public SubjectExcelListener(EduSubjectService eduSubjectService) {
        this.eduSubjectService = eduSubjectService;
    }

    /**
     * 一行一行去读取excel文件
     * @param subjectExcel
     * @param analysisContext
     */
    @Override
    public void invoke(SubjectExcel subjectExcel, AnalysisContext analysisContext) {
        if(subjectExcel == null) {
            throw new GuliException(20001, "文件数据为空");
        }

        //一行一行读取, 每次读取都有两个值, 第一个值为 一级分类, 第二个值为 二级分类
        //判断一级分类是否重复
        EduSubject existOneSubject = this.existOneSubject(subjectExcel.getOneSubjectName());
        if(existOneSubject == null) {// == null 则说明 没有相同的一级分类, 可以进行添加
            existOneSubject = new EduSubject();
            existOneSubject.setParentId("0");
            existOneSubject.setTitle(subjectExcel.getOneSubjectName());//subjectExcel一行excel记录 one是一级分类, two是二级分类

            eduSubjectService.save(existOneSubject);
        }

        //1. 由于一行数据 都是有两个值, 一级分类, 以及该一级分类对应的二级分类
        //2. 而上面添加了一级分类, 那么此处添加的二级分类的父分类(一级分类) 的id 就是该二级分类的parent_id
        //3-1. mybatis-plus进行insert操作后会将自增的id回显至insert时传入的对象中
        //3-2. 如果因为一级分类重复而没有进行insert操作那么既然一级分类重复, 肯定是因为数据库中查到了该一级分类所以不能插入
        //     那么就直接使用 查出来的 一级分类的id (虽然一级分类重复了, 但是该二级分类就是属于该一级分类的)
        //4. 所以此处可以直接使用 existOneSubject.getId()
        String parent_id = existOneSubject.getId();//该二级分类对应的 一级分类的id
        
        
        //判断二级分类是否重复
        EduSubject existTwoSubject = this.existTwoSubject(subjectExcel.getTwoSubjectName(), parent_id);
        if(existTwoSubject == null) {
            existTwoSubject = new EduSubject();
            existTwoSubject.setParentId(parent_id);
            existTwoSubject.setTitle(subjectExcel.getTwoSubjectName());//subjectExcel一行excel记录 one是一级分类, two是二级分类

            eduSubjectService.save(existTwoSubject);
        }


    }

    /**
     * 判断一级分类不能重复添加
     *
     * @param oneSubjectName
     * @return
     */
    private EduSubject existOneSubject(String oneSubjectName) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        //select * from edu_subject where title = ? and parent_id = '0'

        //查出来数据, 则说明该一级分类已经有了, 不能再次添加
        wrapper.eq("title", oneSubjectName);
        wrapper.eq("parent_id", "0");
        EduSubject one = eduSubjectService.getOne(wrapper);

        return one;//为null则说明表中没有该一级分类的记录, 可以添加
    }

    /**
     * 判断二级分类不能重复添加
     *
     * @param twoSubjectName
     * @return
     */
    private EduSubject existTwoSubject(String twoSubjectName, String parent_id) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        //select * from edu_subject where title = ? and parent_id = ?
        //同一个父分类(一级分类)的情况下, 二级分类不能重复

        //查出来数据, 则说明该二级分类已经有了(在同一个 一级分类下已经有了该二级分类), 不能再次添加
        wrapper.eq("title", twoSubjectName);
        wrapper.eq("parent_id", parent_id);
        EduSubject two = eduSubjectService.getOne(wrapper);

        return two;//为null则说明表中没有该一级分类的记录, 可以添加
    }





    /**
     * 读取完成后会调用该方法
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
