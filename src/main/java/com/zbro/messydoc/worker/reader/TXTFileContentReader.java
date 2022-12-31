package com.zbro.messydoc.worker.reader;

import com.zbro.messydoc.commons.document.DocTypeEnum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

public class TXTFileContentReader implements FileContentReader {


    /**
     * Doesn't read the file with size exceeding 10M ,consider it not a valuable document.
     * For the plain text file, only read first 200 lines.
     */
    private final long maxSize = 10485760;
    private final long maxLine = 200;
    @Override
    public DocTypeEnum getType() {
        return DocTypeEnum.txt;
    }

    @Override
    public String read(String path) {
        Charset cs = CharSetDetector.detect(path);
        if (cs != null) {
                try( BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs))){
                    return Arrays.toString(reader.lines().limit(maxLine).toArray(String[]::new));
                }catch (Exception e){
                    e.printStackTrace();
                    return "null";
                }
        } else return "null";

    }
}
