package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文件工具类
 * @author liyuanwei
 */
public class ZipUtils {


    private static int BUFFER_SIZE = 1024;

    /**
     * 压缩成ZIP
     * @param files
     * @param outputStream
     * @throws RuntimeException
     */
    public static void toZip(List<File> files, OutputStream outputStream) throws RuntimeException {

        long start = System.currentTimeMillis();
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(outputStream);
            for (File file : files) {
                byte[] buf = new byte[BUFFER_SIZE];
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                int len;
                FileInputStream in = new FileInputStream(file);
                while ((len = in.read(buf)) != -1){
                    zipOutputStream.write(buf, 0, len);
                }
                zipOutputStream.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zipOutputStream != null){
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
