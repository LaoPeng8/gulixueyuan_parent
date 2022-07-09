package org.pjj.eduservice.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 对应 Excel 的实体类
 * @author PengJiaJun
 * @Date 2022/2/28 22:33
 */
@Data
public class SubjectExcel {

    @ExcelProperty(value = "一级分类", index = 0)
    private String oneSubjectName;

    @ExcelProperty(value = "二级分类", index = 1)
    private String twoSubjectName;

}
