package org.pjj.eduservice.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

/**
 * EasyExcel读取excel需要该监听器(继承至 AnalysisEventListener<实体类> )
 *
 * @author PengJiaJun
 * @Date 2022/2/28 21:32
 */
public class ExcelListener extends AnalysisEventListener<DemoData> {

    /**
     * 一行一行的去读取excel内容
     * @param demoData
     * @param analysisContext
     */
    @Override
    public void invoke(DemoData demoData, AnalysisContext analysisContext) {
        System.out.println("****" + demoData);
    }

    /**
     * 读取表头
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头: " + headMap);
    }

    /**
     * 读取完成后会调用该方法
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("读取完成...");
    }
}
