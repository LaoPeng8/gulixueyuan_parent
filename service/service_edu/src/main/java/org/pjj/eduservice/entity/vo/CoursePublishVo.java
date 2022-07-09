package org.pjj.eduservice.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author PengJiaJun
 * @Date 2022/3/9 15:04
 */
@Data
public class CoursePublishVo implements Serializable {

    /**
     * 忘记加 get, set 方法(@Data) 所报的错, 我还以为是没有实现 Serializable接口...
     * org.springframework.http.converter.HttpMessageConversionException:
     * Type definition error: [simple type, class org.pjj.eduservice.entity.vo.CoursePublishVo];
     * nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
     * No serializer found for class org.pjj.eduservice.entity.vo.CoursePublishVo and no properties discovered to create BeanSerializer
     * (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain:
     * org.pjj.commonutils.R["data"]->java.util.HashMap["coursePublish"])
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String description;
    private String cover;
    private Integer lessonNum;
    private String subjectOne;
    private String subjectTwo;
    private String teacherName;
    private BigDecimal price;

}
