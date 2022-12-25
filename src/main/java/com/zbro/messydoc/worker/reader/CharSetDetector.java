package com.zbro.messydoc.worker.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

public class CharSetDetector {
    private static String[] charset={
            "ASCII",
            "UTF8",
            "GB2312",
            "BIG5",
            "GBK",
            "GB18030",
            "UTF16",
            "UNICODE"
    };
    public static Charset detect(String filePath){
        try {

            for(String cs:charset){
                CharsetDecoder charsetDecoder = Charset.forName(cs).newDecoder();
                try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),charsetDecoder));
                reader.readLine();
                reader.close();
                return Charset.forName(cs);
            }catch (MalformedInputException e){

                }
        }

        }catch (Exception e){
            return null;
        }

        return null;
    }
}
