package org.pjj.eduservice.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 经常会接触到VO，DO，DTO的概念
 *
 * 一般我们的entity对象 或者说 pojo对象 或者说 domain对象 都是普通的实体类对象 是用于 java 与 数据库 交互的对象
 *
 * 而本类, 数据 vo 类, 也就是 (value object 或 view object) 指的是  前端 也就是 View 与 Controller 交互的对象
 *
 * @author PengJiaJun
 * @Date 2022/2/14 22:51
 */
@ApiModel(value = "Teacher查询对象", description = "讲师查询对象封装")
@Data
public class TeacherQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教师名称, 模糊查询")
    private String name;

    @ApiModelProperty(value = "头衔  1 高级讲师  2 首席讲师")
    private Integer level;

    @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
    private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换

    @ApiModelProperty(value = "查询结束时间", example = "2019-12-01 10:10:10")
    private String end;

}
