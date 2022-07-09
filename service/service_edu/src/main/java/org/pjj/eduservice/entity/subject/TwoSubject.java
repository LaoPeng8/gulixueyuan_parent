package org.pjj.eduservice.entity.subject;

import lombok.Data;

/**
 * vo 类
 * controller 与 view    前端与后端交互的类
 * 普通的实体类是 后端与数据库交互的类
 *
 * 二级分类 (二级分类没有子分类了)
 *
 * @author PengJiaJun
 * @Date 2022/3/1 17:23
 */
@Data
public class TwoSubject {

    private String id;

    private String title;

}
