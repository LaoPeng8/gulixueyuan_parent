package org.pjj.eduservice.entity.frontvo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author PengJiaJun
 * @Date 2022/3/27 13:02
 */
@Data
public class CourseFrontVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;//课程名称

    private String teacherId;//讲师id

    private String subjectParentId;//一级分类

    private String subjectId;//二级分类

    private String buyCountSort;//销量排序

    private String gmtCreateSort;//最新时间排序

    private String priceSort;//价格排序

}
