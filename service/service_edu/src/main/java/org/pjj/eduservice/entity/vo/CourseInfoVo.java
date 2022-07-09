package org.pjj.eduservice.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author PengJiaJun
 * @Date 2022/3/3 10:29
 */
@Data
public class CourseInfoVo {

    @ApiModelProperty(value = "课程ID")
    @TableId(value = "id", type = IdType.INPUT) //主键自己输入 (vo类是与前端交互的) (所以不需要数据库id自增)
    private String id;

    @ApiModelProperty(value = "课程讲师ID")
    private String teacherId;

    @ApiModelProperty(value = "课程专业ID (二级分类)")
    private String subjectId;

    @ApiModelProperty(value = "课程分类id (一级分类)")
    private String subjectParentId;

    @ApiModelProperty(value = "课程标题")
    private String title;

    @ApiModelProperty(value = "课程销售价格，设置为0则可免费观看")
    private BigDecimal price;//BigDecimal提供精确的浮点数运算(double不精确)

    @ApiModelProperty(value = "总课时")
    private Integer lessonNum;

    @ApiModelProperty(value = "课程封面图片路径")
    private String cover;

    @ApiModelProperty(value = "课程简介")
    private String description;

}
