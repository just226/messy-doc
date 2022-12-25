//package com.zbro.showdog.testmain;
//
//import com.alibaba.excel.context.AnalysisContext;
//import com.alibaba.excel.event.AnalysisEventListener;
//import com.alibaba.excel.util.ListUtils;
//import com.alibaba.fastjson2.JSON;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {
//    /**
//     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
//     */
//    private List<Map<Integer, String>> cachedDataList = new ArrayList<>();
//
//    @Override
//    public void invoke(Map<Integer, String> data, AnalysisContext context) {
//        cachedDataList.add(data);
//    }
//
//    @Override
//    public void doAfterAllAnalysed(AnalysisContext context) {
//        log.info("所有数据解析完成！");
//        log.info("everthing: {}" ,JSON.toJSONString(cachedDataList));
//    }
//
//
//}