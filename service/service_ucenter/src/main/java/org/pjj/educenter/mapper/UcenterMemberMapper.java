package org.pjj.educenter.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pjj.educenter.entity.UcenterMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author pjj
 * @since 2022-03-21
 */
@Repository
public interface UcenterMemberMapper extends BaseMapper<UcenterMember> {

    //查询某一天注册人数 day = "2022-4-3"
    Integer registerCountDay(String day);
}
