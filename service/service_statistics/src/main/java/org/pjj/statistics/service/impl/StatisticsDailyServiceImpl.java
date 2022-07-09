package org.pjj.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.RandomUtils;
import org.pjj.commonutils.R;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.pjj.statistics.client.UcenterMember;
import org.pjj.statistics.entity.StatisticsDaily;
import org.pjj.statistics.mapper.StatisticsDailyMapper;
import org.pjj.statistics.service.StatisticsDailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 网站统计数据 服务实现类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-04-03
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Autowired
    private UcenterMember ucenterMember;

    //统计某一天注册人数, 生成统计数据, 并插入表中
    @Override
    public void registerCount(String day) {
        //添加记录之前先删除表中相同日期的数据(就是说 统计的某天的数据 只能有 一条, 如2022-04-03表中已经有一条了, 则再次统计2022-04-03时, 先删除原有的)
        UpdateWrapper<StatisticsDaily> wrapper = new UpdateWrapper<>();
        wrapper.eq("date_calculated", day);
        baseMapper.delete(wrapper);

        //远程调用 ucenter服务提供的 得到某一天的注册人数
        R r = ucenterMember.registerCount(day);
        if(r.getCode() != 20000) {//返回code码不是 成功, 则停止继续操作
            throw new GuliException(20001, "远程调用失败");
        }

        Integer registerCount = (Integer) r.getData().get("registerCount");//获取到 某一天 的注册人数

        //将获取获取到的注册人数, 添加到 统计表中
        StatisticsDaily statistics = new StatisticsDaily();
        statistics.setRegisterNum(registerCount);//注册人数
        statistics.setDateCalculated(day);//统计日期

        //这三个值老师使用随机数代替了没有真正的查询出来 (而我也没有太好的思路查询出来(感觉需要修改数据库表才行, 现有的表根本查不出来), 就也使用随机数了)
        statistics.setVideoViewNum(RandomUtils.nextInt(100, 200));//每日播放视频数
        statistics.setLoginNum(RandomUtils.nextInt(100, 200));//登录人数
        statistics.setCourseNum(RandomUtils.nextInt(100, 200));//每日新增课程数

        baseMapper.insert(statistics);

    }


    /**
     * 统计图表显示, 返回两部分数据, 日期数组, 数量数组
     * @param type 1 表示查询 登录人数, 2 表示查询 注册人数, ...
     * @param begin 查询几日到几日之间的 type
     * @param end 查询几日到几日之间的 type
     * @return
     */
    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {

        String column = "";
        switch(type){
            case "1" :
                column = "login_num";
                break;
            case "2" :
                column = "register_num";
                break;
            case "3" :
                column = "video_view_num";
                break;
            case "4" :
                column = "course_num";
                break;
            default :
                column = "register_num";//默认
        }


        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated", begin, end);//查询 日期在 begin ~ end 之间的数据
        wrapper.select("date_calculated", column);//只查询 data_calculated字段 与 传入的type所对应需要的字段column

        List<StatisticsDaily> statisticsDailies = baseMapper.selectList(wrapper);

        //将查询出来的数据statisticsDailies, 封装为前端想要的数据类型 (两个json数组)
        List<String> xData = new ArrayList<>();//横坐标数据x
        List<Integer> yData = new ArrayList<>();//纵坐标数据y
        for(StatisticsDaily temp : statisticsDailies) {
            xData.add(temp.getDateCalculated());//横坐标数组存放 日期
//            yData.add();//纵坐标数组存放 注册人数(代指 type 用户想要的 注册人数 或 登录人数 或 ...)
            switch(type){
                case "1" :
                    yData.add(temp.getLoginNum());
                    break;
                case "2" :
                    yData.add(temp.getRegisterNum());
                    break;
                case "3" :
                    yData.add(temp.getVideoViewNum());
                    break;
                case "4" :
                    yData.add(temp.getCourseNum());
                    break;
                default :
                    yData.add(temp.getRegisterNum());//默认
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("x_data", xData);
        map.put("y_data", yData);

        return map;
    }
}
