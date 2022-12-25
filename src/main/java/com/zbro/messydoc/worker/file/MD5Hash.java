package com.zbro.messydoc.worker.file;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class MD5Hash {
    public static String digest(Path filePath){
        try{
            byte[] data = Files.readAllBytes(filePath);
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            return new BigInteger(1,hash).toString(16);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static String digest(String str){
        try{
            byte[] data = str.getBytes();
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            return new BigInteger(1,hash).toString(16);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
