package org.pjj.statistics.controller;


import org.pjj.commonutils.R;
import org.pjj.statistics.service.StatisticsDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-04-03
 */
@RestController
@RequestMapping("/edustatistics/sta")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class StatisticsDailyController {

    @Autowired
    private StatisticsDailyService statisticsDailyService;

    //统计某一天注册人数, 生成统计数据, 并插入表中
    @PostMapping("/registerCount/{day}")
    public R registerCount(@PathVariable String day) {

        statisticsDailyService.registerCount(day);

        return R.ok();
    }

    //统计图表显示, 返回两部分数据, 日期json数组, 数量json数组
    @GetMapping("/showData/{type}/{begin}/{end}")
    public R showData(@PathVariable String type, @PathVariable String begin, @PathVariable String end) {

        Map<String, Object> map = statisticsDailyService.getShowData(type, begin, end);

        return R.ok().data(map);
    }

}

