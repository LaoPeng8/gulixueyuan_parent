package org.pjj.eduservice.service;

import org.pjj.eduservice.entity.EduChapter;
import com.baomidou.mybatisplus.extension.service.IService;
import org.pjj.eduservice.entity.chapter.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author pjj
 * @since 2022-03-02
 */
public interface EduChapterService extends IService<EduChapter> {

    //课程大纲列表, 根据课程id查询
    List<ChapterVo> getChapterVideoByCourseId(String courseId);

    //删除章节 , 待删除的章节如果有小结 即 不让其删除
    boolean deleteChapter(String chapterId);
}
