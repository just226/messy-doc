package com.zbro.messydoc.worker.document;

import com.zbro.messydoc.commons.document.DocTypeEnum;

import java.nio.file.Files;
import java.nio.file.Path;

public class DocTypeDetector {
    public boolean isDocTypefile(Path file) {

        return false;
    }

    public static DocTypeEnum getFileType(Path file) {
        try {

            String originalType = Files.probeContentType(file);
            if(originalType == null){
                return DocTypeEnum.unknown;
            }
            switch (originalType){
                case ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"):
                    return DocTypeEnum.EXCEL;
                case "application/vnd.ms-excel":
                    return DocTypeEnum.EXCEL;
            }
            return DocTypeEnum.unknown;
        } catch (Exception e) {
            e.printStackTrace();
            return DocTypeEnum.unknown;
        }
    }

    public static DocTypeEnum getTypeFromExtensionName(Path file) {
        try {

            String[] name = file.getFileName().toString().split("\\.");

            switch (name[name.length - 1]) {
                case "xlsx":
                    return DocTypeEnum.EXCEL;
                case "xls":
                    return DocTypeEnum.EXCEL;
//                case "xlsm":
//                    return DocTypeEnum.EXCEL;
                case "docx":
                    return DocTypeEnum.WORD;
                case "doc":
                    return DocTypeEnum.WORD;
                case "pptx":
                    return DocTypeEnum.PPT;
                case "ppt":
                    return DocTypeEnum.PPT;
                case "csv":
                    return DocTypeEnum.csv;
                case "txt":
                    return DocTypeEnum.txt;
                case "pdf":
                    return DocTypeEnum.pdf;
                case "zip":
                    return DocTypeEnum.zip;
                case "rar":
                    return DocTypeEnum.rar;
                case "gz":
                    return DocTypeEnum.gz;
            }
            return DocTypeEnum.unknown;
        } catch (Exception e) {
            e.printStackTrace();
            return DocTypeEnum.unknown;
        }
    }
}
