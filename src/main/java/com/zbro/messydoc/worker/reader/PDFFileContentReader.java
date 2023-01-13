package com.zbro.messydoc.worker.reader;


import com.zbro.messydoc.commons.document.DocTypeEnum;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

public class PDFFileContentReader implements FileContentReader {
    @Override
    public DocTypeEnum getType() {
        return DocTypeEnum.pdf;
    }

    @Override
    public String read(String path) {
        try(PDDocument document = PDDocument.load(new File(path))){
            return new PDFTextStripper().getText(document);
        }catch (Exception e){
            return "null";
        }
    }
}
