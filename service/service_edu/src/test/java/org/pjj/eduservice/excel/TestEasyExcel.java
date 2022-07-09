package org.pjj.eduservice.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试 easyExcel 的写操作 (写入数据至 excel)
 * @author PengJiaJun
 * @Date 2022/2/27 23:46
 */
public class TestEasyExcel {
    public static void main(String[] args) {
        //实现excel写的操作
//        testEasyExcel01();
//        testEasyExcel02();
        testEasyExcel03();

    }

    /**
     * 写入 excel 第一种写法 (不需要手动关闭流)
     */
    public static void testEasyExcel01() {
        // 1. 设置写入文件夹地址 和 excel文件名称
        String fileName = "D://java_excel.xlsx";

        // 2. 调用easyExcel里面的方法实现写操作
        // 第一个参数文件路径文件名, 第二个参数excel对应的实体类
        EasyExcel.write(fileName, DemoData.class).sheet("学生列表").doWrite(getData());
    }

    /**
     * 写入 excel 第二种写法 (需要手动关闭流)
     */
    public static void testEasyExcel02() {
        // 1. 设置写入文件夹地址 和 excel文件名称
        String fileName = "D://java_excel0002.xlsx";

        // 2. 调用easyExcel里面的方法实现写操作
        ExcelWriter excelWriter = EasyExcel.write(fileName, DemoData.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("学生列表2222").build();
        excelWriter.write(getData(), writeSheet);//写入数据, 以及指定 sheet

        //关闭流
        excelWriter.finish();
    }

    public static List<DemoData> getData() {
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData demoData = new DemoData();
            demoData.setSno(i + 1);
            demoData.setSname("lucy_" + (i + 1));
            list.add(demoData);
        }

        return list;
    }

    /**
     * 测试 读取 excel
     */
    public static void testEasyExcel03() {
        // 1. 设置写入文件夹地址 和 excel文件名称
        String fileName = "D://java_excel0002.xlsx";
        EasyExcel.read(fileName, DemoData.class, new ExcelListener()).sheet().doRead();
    }

}
