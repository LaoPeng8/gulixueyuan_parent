package org.pjj.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.eduservice.entity.EduChapter;
import org.pjj.eduservice.entity.EduVideo;
import org.pjj.eduservice.entity.chapter.ChapterVo;
import org.pjj.eduservice.entity.chapter.VideoVo;
import org.pjj.eduservice.mapper.EduChapterMapper;
import org.pjj.eduservice.mapper.EduVideoMapper;
import org.pjj.eduservice.service.EduChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author pjj
 * @since 2022-03-02
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduChapterMapper eduChapterMapper;

    @Autowired
    private EduVideoMapper eduVideoMapper;

    //课程大纲列表, 根据课程id查询
    @Override
    public List<ChapterVo> getChapterVideoByCourseId(String courseId) {

        List<ChapterVo> result = new ArrayList<>();//最终返回的 某个课程的 所有章节 (每个章节包含各自的小结)

        QueryWrapper<EduChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);//根据课程id查询 该课程 所有的章节
        List<EduChapter> eduChapters = eduChapterMapper.selectList(wrapper);//查询 某课程 所有的章节

        QueryWrapper<EduVideo> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("course_id", courseId);//根据课程id查询 该课程 所有的小结
        List<EduVideo> eduVideos = eduVideoMapper.selectList(wrapper2);//查询 某课程 所有的小结

        for(EduChapter eduChapter : eduChapters) {//遍历所有章节
            //封装为 前端需要的章节VO类
            ChapterVo chapterVo = new ChapterVo();
            chapterVo.setId(eduChapter.getId());
            chapterVo.setTitle(eduChapter.getTitle());
            chapterVo.setSort(eduChapter.getSort());

            ArrayList<VideoVo> videoVoList = new ArrayList<>();//用于封装 每个章节 的小结集合
            for(EduVideo eduVideo : eduVideos) {//遍历所有小结
                //封装为 前端需要的小结VO类
                VideoVo videoVo = new VideoVo();
                videoVo.setId(eduVideo.getId());
                videoVo.setTitle(eduVideo.getTitle());
                videoVo.setSort(eduVideo.getSort());
                videoVo.setIsFree(eduVideo.getIsFree());
                videoVo.setVideoSourceId(eduVideo.getVideoSourceId());
                videoVo.setVideoOriginalName(eduVideo.getVideoOriginalName());

                //如果 某个小结的章节id(chapter_id) == 章节的章节id 说明该小结属于这个章节
                if(eduChapter.getId().equals(eduVideo.getChapterId())){
                    videoVoList.add(videoVo);//将 "正确" 的 小结 加入 对应的章节
                }
            }
            chapterVo.setChildren(videoVoList);//将 每个章节的 小结集合 赋值给该章节

            result.add(chapterVo);//将 章节 加入 最终返回的 集合中(某给课程的 所有章节)
        }


        return result;
    }


    // 删除章节的方法
    @Override
    public boolean deleteChapter(String chapterId) {
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id", chapterId);
        Integer count = eduVideoMapper.selectCount(wrapper);
        if(count > 0) {
            throw new GuliException(20001, "该章节的小结不为空, 不能删除该章节");
        }

        int i = eduChapterMapper.deleteById(chapterId);//删除章节
        return i > 0;
    }
}
