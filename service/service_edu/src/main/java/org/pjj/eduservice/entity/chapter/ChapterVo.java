package org.pjj.eduservice.entity.chapter;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 章节 VO 类 (与前端交互的类) (或用来 接收前端的数据) (或用来 返回数据给前端)
 *
 * 一个章节类 对应 多个小结类
 * @author PengJiaJun
 * @Date 2022/3/6 15:18
 */
@Data
public class ChapterVo {

    private String id;

    private String title;

    private Integer sort;

    //一个章节类 对应 多个小结类
    private List<VideoVo> children;

}
