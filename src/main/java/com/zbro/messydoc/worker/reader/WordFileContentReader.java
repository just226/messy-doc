package com.zbro.messydoc.worker.reader;


import com.zbro.messydoc.commons.document.DocTypeEnum;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.ooxml.extractor.ExtractorFactory;

import java.io.File;

public class WordFileContentReader implements FileContentReader {
    @Override
    public DocTypeEnum getType() {
        return DocTypeEnum.WORD;
    }

    @Override
    public String read(String path) {
        try{
            POITextExtractor extractor = ExtractorFactory.createExtractor(new File(path));
            String s = extractor.getText();
            extractor.close();
            return s;
        }catch (Exception e){
            return "null";
        }
    }
}
