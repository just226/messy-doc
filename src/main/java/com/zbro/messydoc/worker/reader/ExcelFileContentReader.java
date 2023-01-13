package com.zbro.messydoc.worker.reader;


import com.zbro.messydoc.commons.document.DocTypeEnum;
import org.apache.poi.ooxml.extractor.ExtractorFactory;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;

import java.io.File;

public class ExcelFileContentReader implements FileContentReader {

    @Override
    public DocTypeEnum getType() {
        return DocTypeEnum.EXCEL;
    }

    @Override
    public String read(String path) {
        try(XSSFExcelExtractor extractor = ExtractorFactory.createExtractor(new File(path))){
           return extractor.getText();
        }catch (Exception e){
            return "null";
        }
    }
}
