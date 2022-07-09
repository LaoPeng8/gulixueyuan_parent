package org.pjj.eduservice.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 对应 excel 的类
 * @author PengJiaJun
 * @Date 2022/2/27 23:16
 */
@Data
public class DemoData {

    //设置 excel 表头名称
    //value的作用是写入excel时, excel的表头名称, 也就是这一列的第一个是什么
    //index的作用是读取excel时, 每一列的值读取至该属性中
    @ExcelProperty(value = "学生编号", index = 0)
    private Integer sno;

    @ExcelProperty(value = "学生姓名", index = 1)
    private String sname;

}
