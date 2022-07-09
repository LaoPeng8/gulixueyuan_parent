package org.pjj.commonutils.exceptionlog;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常输出 工具类
 * 在 输入 自定义异常的信息时 会发现只有一行数据 e.getMsg()
 * 比如:
 * log.error(e.getMsg());//打印日志 (将error日志写入文件)
 * //该行代码输出错误信息只有
 * 2022-02-15 23:40:18.315 [http-nio-8001-exec-8] ERROR o.p.s.exceptionhandler.GlobalExceptionHandler - 抛出了自定义异常...
 *
 * 所以需要将 e.printStackTrace() 的错误信息通过 字符流的方式 最后返回为 String类型的字符串
 *
 * 那么以后输出错误信息就可以这样输出
 * log.error(ExceptionUtil.getMessage(e)); //就可以将 e.printStackTrace() 的详细错误信息输出
 *
 * @author PengJiaJun
 * @Date 2022/2/15 23:50
 */
public class ExceptionUtil {

    public static String getMessage(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);//PrintWriter是输出流, 可以输出到File, 也可以输出到另外一个流中, 此处就是输出到 StringWriter 中

            e.printStackTrace(pw);// 将出错的栈信息输出到printWriter中
            pw.flush();//将 数据从缓冲区刷出来 (相当于将 pw的数据 写入 sw中)
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();//输出 sw 中的错误信息 (将sw中的错误信息转为String, 那么就可以直接打印到控制台了)
    }
}
