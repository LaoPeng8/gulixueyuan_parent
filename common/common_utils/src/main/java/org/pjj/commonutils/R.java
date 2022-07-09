package org.pjj.commonutils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义 后台返回前台的 json 类
 * @author PengJiaJun
 * @Date 2022/2/14 16:10
 */
@Data
public class R {

    @ApiModelProperty(value = "是否成功") // Swagger
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    //私有构造方法
    private R() {}

    //成功的静态方法
    public static R ok() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }
    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        r.setData(map);
        return r;
    }

    //失败的静态方法
    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }
    public static R error(Map<String, Object> map) {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        r.setData(map);
        return r;
    }

    //失败的静态方法
    public static R warn() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.WARNING);
        r.setMessage("失败");
        return r;
    }

    // 链式调用 设置 参数
    public R success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }


}
