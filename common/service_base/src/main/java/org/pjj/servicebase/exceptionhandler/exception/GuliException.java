package org.pjj.servicebase.exceptionhandler.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 * @author PengJiaJun
 * @Date 2022/2/15 20:50
 */
@Data // get set toString
@AllArgsConstructor //有参构造
@NoArgsConstructor //无参构造
public class GuliException extends RuntimeException {

    private Integer code;//状态
    private String msg;//异常信息

    @Override
    public String toString() {
        return "GuliException{" +
                "message=" + this.getMessage() +
                ", code=" + code +
                '}';
    }

}
