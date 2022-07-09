package org.pjj.comment.entity.vo;

import lombok.Data;
import org.pjj.comment.entity.EduComment;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author PengJiaJun
 * @Date 2022/3/29 17:46
 */
@Data
public class CommentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 评论数据
    private String id;
    private String parentId;
    private String courseId;
    private String teacherId;
    private String memberId;
    private String nickname;
    private String avatar;
    private String content;
    private Boolean isDeleted;
    private Date gmtCreate;
    private Date gmtModified;

    private Map<String, Object> commentTwo;

}
