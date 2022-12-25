package com.zbro.messydoc.worker.reader;



import com.zbro.messydoc.commons.document.DocTypeEnum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

public class CSVFileContentReader implements FileContentReader {

    /**
     * For the plain text file, only read first 200 lines.
     */
    private final long maxLine = 200;


    @Override
    public DocTypeEnum getType() {
        return DocTypeEnum.csv;
    }

    @Override
    public String read(String path) {
        Charset cs = CharSetDetector.detect(path);
        if (cs != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
                String s = Arrays.toString(reader.lines().limit(maxLine).toArray(String[]::new));
                reader.close();
                return s;
            } catch (Exception e) {
                e.printStackTrace();
                return "null";
            }
        } else return "null";

    }
}
