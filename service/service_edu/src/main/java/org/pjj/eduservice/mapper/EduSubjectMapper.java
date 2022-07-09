package org.pjj.eduservice.mapper;

import org.apache.ibatis.annotations.Select;
import org.pjj.eduservice.entity.EduSubject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程科目 Mapper 接口
 * </p>
 *
 * @author pjj
 * @since 2022-02-28
 */
@Repository
public interface EduSubjectMapper extends BaseMapper<EduSubject> {
}
