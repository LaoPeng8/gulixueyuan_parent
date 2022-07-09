package org.pjj.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.pjj.commonutils.R;
import org.pjj.eduservice.client.VodClient;
import org.pjj.eduservice.entity.EduVideo;
import org.pjj.eduservice.service.EduVideoService;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author pjj
 * @since 2022-03-02
 */
@RestController
@RequestMapping("/eduservice/video")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class EduVideoController {

    @Autowired
    private EduVideoService eduVideoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private VodClient vodClient;// 该 接口 中定义了 调用 service-vod 服务的 方法(feign 调用)

    //添加小节
    @PostMapping("/addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo) {

        boolean flag = eduVideoService.save(eduVideo);

        if (flag){
            return R.ok();
        }
        return R.error();
    }

    //删除小节 (同时会删除小节中的视频)
    @DeleteMapping("/deleteVideo/{id}")
    public R deleteVideo(@PathVariable String id) {
        boolean flag = true;
        EduVideo eduVideo = eduVideoService.getById(id); //根据 小节 id, 查询出 小节中的视频id
        if(!StringUtils.isEmpty(eduVideo.getVideoSourceId())) {
            // 视频id 不为空则删除该视频 (调用 serv)
            R r = vodClient.deleteAliyunVideo(eduVideo.getVideoSourceId());//删除小节中的视频
            if (r.getCode() == 20000) {//删除小节视频成功, 才会删除小节
                flag = eduVideoService.removeById(id);//删除小节
            } else {
                throw new GuliException(20001, "删除小节视频失败, 熔断器...");
            }
        }

        if (flag){
            return R.ok();
        }
        return R.error();
    }

    //修改小节
    @PutMapping("/updateVideo")
    public R updateVideo(@RequestBody EduVideo eduVideo) {

        boolean flag = eduVideoService.updateById(eduVideo);

        if(flag) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 传入 视频id 找到该视频 并且播放次数 +1
     * @param videoSourceId 视频id(不是主键id)
     * @return
     */
    @PutMapping("/playCount/{videoSourceId}")
    public R updateVideoPlayCount(@PathVariable String videoSourceId, HttpServletRequest request) {


        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();
        String ipValue = redisTemplate.opsForValue().get("video"+":"+ip+":"+videoSourceId);
        if(StringUtils.isEmpty(ipValue)) {//如果从redis下取出key为该ip+id的value 取不到, 说明该ip下10分钟内没有访问过该视频, 则让该视频浏览数 +1
            QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
            wrapper.eq("video_source_id", videoSourceId);
            EduVideo one = eduVideoService.getOne(wrapper);
            one.setPlayCount(one.getPlayCount() + 1);

            eduVideoService.updateById(one);

            redisTemplate.opsForValue().set("video"+":"+ip+":"+videoSourceId, "1", 10, TimeUnit.MINUTES);//将访问该接口的key=ip+id与value=1加入redis 过期时间 10 分钟
        }
        //如果取出了key为该ip+id的value, 则说明该ip10分钟内已经访问过该视频了, 则啥也不干(视频浏览数 不加1)

        return R.ok();
    }


}

