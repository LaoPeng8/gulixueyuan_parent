package org.pjj.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.pjj.commonutils.R;
import org.pjj.eduservice.entity.EduTeacher;
import org.pjj.eduservice.entity.vo.TeacherQuery;
import org.pjj.eduservice.service.EduTeacherService;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author pjj
 * @since 2022-02-12
 */
@Api(value = "讲师管理", tags = {"讲师管理"}) //swagger 定义该Controller的中文解释
@RestController
@RequestMapping("/eduservice/teacher")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class EduTeacherController {

    @Autowired
    private EduTeacherService eduTeacherService;

    /**
     * 查询讲师表所有数据
     */
    @ApiOperation("所有讲师列表") //swagger 定义该 请求 的中文解释
    @GetMapping("/findAll")
    public R findAllTeacher() {
        List<EduTeacher> list = eduTeacherService.list(null);
        if(list.size() == 0){
            return R.ok();
        }
        return R.ok().data("items", list);
    }

    /**
     * 根据 id 删除讲师 (逻辑删除)
     * @param id
     * @return
     */
    @ApiOperation("根据id删除讲师") //swagger 定义该 请求 的中文解释
    @DeleteMapping("/del/{id}")
    public R removeTeacher(@ApiParam(name = "id", value = "讲师ID", required = true) // swagger 定义该 参数 的中文解释
                                     @PathVariable String id) {
        boolean flag = eduTeacherService.removeById(id);

        if (flag){
            return R.ok();
        }

        return R.error();
    }


    /**
     * 分页查询讲师
     * @param current 当前页
     * @param limit 每页的记录数
     * @return
     */
    @ApiOperation("分页查询讲师")
    @GetMapping("/pageTeacher/{current}/{limit}")
    public R pageListTeacher(@ApiParam("当前页") @PathVariable Integer current,
                             @ApiParam("每页数据条数") @PathVariable Integer limit) {

        if (limit > 10 || limit < 1){
            limit = 8;
        }

        int count = eduTeacherService.count(null);//总记录数
        int currentMAX = count % limit > 0 ? (count / limit) + 1 : count / limit;//最大的页码
        if(current < 1) {
            current = 0;
        }
        if(current > currentMAX) {
            current = currentMAX;
        }

        Page<EduTeacher> pageTeacher = new Page<>(current, limit);

        eduTeacherService.page(pageTeacher, null);

        return R.ok().data("total", pageTeacher.getTotal()) //总记录数
                .data("rows", pageTeacher.getRecords()); //数据
    }

    /**
     * 带条件查询的  分页查询讲师
     *
     * 注意: @RequestBody TeacherQuery teacherQuery 使用 @RequestBody 来获取 前端传递的 json 值, 是没错的
     * 但是 请求不能是 @GetMapping 也就是说 不能是get请求, 必须是post请求
     * @param current 当前页
     * @param limit 每页的记录数
     * @return
     */
    @ApiOperation("分页查询讲师")
    @PostMapping("/pageTeacherCondition/{current}/{limit}")
    public R pageListTeacherCondition(@ApiParam("当前页") @PathVariable Integer current,
                                      @ApiParam("每页数据条数") @PathVariable Integer limit,
                                      @ApiParam("分页查询条件") @RequestBody(required = false) TeacherQuery teacherQuery) {

        Page<EduTeacher> pageTeacher = new Page<>(current, limit);

        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();//where条件
        //判断条件值是否为空, 如果不为空则拼接条件
        if(!StringUtils.isEmpty(teacherQuery.getName())) { // name 不为空
            wrapper.like("name", teacherQuery.getName());
        }
        if(!StringUtils.isEmpty(teacherQuery.getLevel())) { // level 不为空
            wrapper.eq("level", teacherQuery.getLevel());
        }
        if(!StringUtils.isEmpty(teacherQuery.getBegin())) { //开始时间 不为空 (有一个问题 前端传的时间格式可能不对, 该怎么判断一下)
            wrapper.ge("gmt_create", teacherQuery.getBegin());// 大于等于 创建时间
        }
        if(!StringUtils.isEmpty(teacherQuery.getEnd())) { //结束时间 不为空
            wrapper.le("gmt_create", teacherQuery.getEnd());  // 小于等于 创建时间
        }

        //根据创建时间排序
        wrapper.orderByDesc("gmt_create");

        eduTeacherService.page(pageTeacher, wrapper);//分页查询

        return R.ok().data("total", pageTeacher.getTotal()) //总记录数
                .data("rows", pageTeacher.getRecords()); //数据
    }


    @ApiOperation("添加讲师")
    @PostMapping("/addTeacher")
    public R addTeacher(@RequestBody EduTeacher eduTeacher){
        eduTeacher.setId(null);//不管前端是否传递id, 此处都不接收, mybatis-plus会id自增
        eduTeacher.setGmtCreate(null);//不接收, mybatis-plus会 自动填充
        eduTeacher.setGmtModified(null);//不接收, mybatis-plus会 自动填充

        //先根据 name 查询有没有 该记录
        EduTeacher teacher = eduTeacherService.getOne(new QueryWrapper<EduTeacher>().eq("name", eduTeacher.getName()));
        if( teacher != null ) {
            return R.error().message("name以被使用, 不能重复");
        }

        //插入操作
        boolean save = eduTeacherService.save(eduTeacher);
        if (save){
            return R.ok();
        }

        return R.error();
    }

    /**
     * 根据 id 查询讲师
     * @param id
     * @return
     */
    @ApiOperation("根据id查询讲师")
    @GetMapping("/getTeacher/{id}")
    public R getTeacher(@PathVariable String id) {

        EduTeacher teacher = eduTeacherService.getById(id);

        if (teacher != null) {
            return R.ok().data("teacher", teacher);
        }

        return R.error();

    }

    @ApiOperation("修改讲师")
    @PutMapping("/updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher eduTeacher) {
        //创建时间与修改时间前端传入的都不接受, 修改讲师不能修改创建时间, 而修改时间 mybatis-plus会自动填充 当前时间 不需要前端传入
        eduTeacher.setGmtCreate(null);
        eduTeacher.setGmtModified(null);

        //由于name有唯一性索引, 所以修改name为已有name的话会报错Duplicate entry 'test' for key 'uk_name';
        //那么由于有全局异常处理类org.pjj.servicebase.exceptionhandler.GlobalExceptionHandler那么则会返回指定的错误信息
        //而不会将后端错误直接的显示给前端展示
        boolean flag = eduTeacherService.updateById(eduTeacher);

        if(flag) {
            return R.ok();
        }

        return R.error();
    }

    @PostMapping("/test/error")
    public R testException() {

        try{
            int i = 10 / 0;
        } catch(Exception e) {
            throw new GuliException(4044, "抛出了自定义异常...");
        }

        return R.ok();
    }


}

