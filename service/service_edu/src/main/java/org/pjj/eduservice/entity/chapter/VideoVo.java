package org.pjj.eduservice.entity.chapter;

import lombok.Data;

/**
 * 小结类 (一个小结 相当于就是一个视频)
 *
 * @author PengJiaJun
 * @Date 2022/3/6 15:29
 */
@Data
public class VideoVo {

    private String id;

    private String title;

    private Integer sort;

    private Boolean isFree;

    private String videoSourceId;

    private String videoOriginalName;

}
