package com.zbro.messydoc.testmain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Integer.toHexString;

public class FileTypeUtils {
    private static String getFileContent(String filePath) throws IOException {

        byte[] b = new byte[20];
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            /**
             * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             *从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             * 之所以从输入流中读取20个字节数据，是因为不同格式的文件头魔数长度是不一样的，比如 EML("44656C69766572792D646174653A")和GIF("47494638")
             * 为了提高识别精度所以获取的字节数相应地长一点
             */
            inputStream.read(b, 0, 20);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return bytesToHexString(b);
    }
    /**
     * @description 第二步：将文件头转换成16进制字符串
     * @param
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
//        System.out.println("文件类型16进制字符串是"+stringBuilder.toString());
        return stringBuilder.toString();
    }
    /**
     * @description 第三步：根据十六进制字符串判断文件类型格式
     * @param filePath 文件路径
     * @return 文件类型
     */
    public static FileType getType(String filePath) throws IOException {
        String fileHead = getFileContent(filePath);
        if (fileHead == null || fileHead.length() == 0) {
            return FileType.UNKNOWN;
        }
        fileHead = fileHead.toUpperCase();
        FileType[] fileTypes = FileType.values();
        for (FileType type : fileTypes) {
//            startsWith() 方法用于检测字符串是否以指定的前缀开始
            if (fileHead.startsWith(type.getValue())) {
                return type;
            }
        }
        System.out.println(filePath + ":" + fileHead);
        return FileType.UNKNOWN;
    }

}
