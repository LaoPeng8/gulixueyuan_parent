package org.pjj.statistics.schedule;

import org.pjj.statistics.service.StatisticsDailyService;
import org.pjj.statistics.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时任务的类
 * @author PengJiaJun
 * @Date 2022/4/3 18:32
 */
@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService statisticsDailyService;

    /**
     * Cron表达式:
     * 6-7个参数, 以空格隔开
     * 秒 分 时 天(月份中的第几天) 月 天(星期中的第几天) 年(年可写可不写)
     */
    // "0/5 * * * * ?" 表示每隔5秒执行一次
//    @Scheduled(cron = "0/5 * * * * ?") //测试方法, 现在不需要
    public void task1() {
        System.out.println("*********************************task1执行了*********************************");
    }

    //在每天凌晨1点, 执行该方法, 将统计数据查询出来并加入数据库
    @Scheduled(cron = "0 0 1 * * ?")
    public void task2() {
        //将当前时间的前一天 的统计数据 查询出来并加入数据库
        statisticsDailyService.registerCount(DateUtil.formatDate(DateUtil.addDays(new Date(), -1)));
    }

}
