package org.pjj.servicebase.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.pjj.commonutils.R;
import org.pjj.commonutils.exceptionlog.ExceptionUtil;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理类
 * @author PengJiaJun
 * @Date 2022/2/15 16:05
 */
@ControllerAdvice //@ExceptionHandler注解默认至能处理本类的异常, 如加上该注解@ControllerAdvice即是可以其他类的异常(即其他类抛出异常, 也会跳转到该类的方法执行)
@Slf4j // 日志
public class GlobalExceptionHandler {

    /**
     * 自定义异常处理类
     * @param e
     * @return
     */
    @ExceptionHandler(GuliException.class) //处理的异常
    @ResponseBody //返回json数据
    public R error(GuliException e) {
        log.error(ExceptionUtil.getMessage(e));//打印日志 (打印在控制台) (将error日志写入文件)
//        e.printStackTrace();

        //抛出异常时可以传入 code 与 message, 通过 此处 R.error() 统一格式返回给前端 搞死 状态码code 与 消息message
        //例如: throw new GuliException(4044, "抛出了自定义异常...");
        return R.error().code(e.getCode()).message(e.getMsg());
    }

    /**
     * 全局 统一异常处理
     *
     * ExceptionHandler(Exception.class) 该注解 会处理本类中 抛出的(Exception.class) 异常, 如果有该异常抛出就会被该注解捕获(跳转到该注解的方法)
     * 然后打印错误信息, 并返回经过封装了的错误信息至前端
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class) //处理的异常
    @ResponseBody //返回json数据
    public R error(Exception e) {
        log.error(ExceptionUtil.getMessage(e));//打印日志 (打印在控制台) (将error日志写入文件)
//        e.printStackTrace();

        return R.error().message("执行了全局异常处理");//前后端分离后 就是可以这样直接返回数据
        //没有前后端分离的话, 此处是可以直接返回页面的, 例如 /error/404 这种
    }

}
