package com.zbro.messydoc.worker.file;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class MD5Hash {
    public static String digestOldVer(Path filePath){
        try{
            //readAllBytes will allocate a large native memory with DirectByteBuffer
            byte[] data = Files.readAllBytes(filePath);
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            return new BigInteger(1,hash).toString(16);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String digest(Path filePath){
        try(FileInputStream inputStream = new FileInputStream(filePath.toString())){
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileChannel channel = inputStream.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(512 * 1024);
            while(channel.read(buff) != -1)
            {
                buff.flip();
                md.update(buff);
                buff.clear();
            }
            byte[] hash = md.digest();
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
