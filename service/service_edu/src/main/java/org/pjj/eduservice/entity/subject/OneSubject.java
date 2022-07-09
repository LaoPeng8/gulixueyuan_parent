package org.pjj.eduservice.entity.subject;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * vo 类
 * controller 与 view    前端与后端交互的类
 * 普通的实体类是 后端与数据库交互的类
 *
 * 一级分类 (一个 一级分类 有 多个二级分类(子分类))
 *
 * @author PengJiaJun
 * @Date 2022/3/1 17:23
 */
@Data
public class OneSubject {

    private String id;

    private String title;

    private List<TwoSubject> children = new ArrayList<>();

}
