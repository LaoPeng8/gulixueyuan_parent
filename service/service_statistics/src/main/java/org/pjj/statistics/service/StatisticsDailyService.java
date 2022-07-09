package org.pjj.statistics.service;

import org.pjj.statistics.entity.StatisticsDaily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计数据 服务类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-04-03
 */
public interface StatisticsDailyService extends IService<StatisticsDaily> {

    //统计某一天注册人数, 生成统计数据, 并插入表中
    void registerCount(String day);

    //统计图表显示, 返回两部分数据, 日期数组, 数量数组
    Map<String, Object> getShowData(String type, String begin, String end);
}
