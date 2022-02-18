package com.jinninghui.datasphere.icreditstudio.framework.utils;


import org.springframework.web.multipart.MultipartFile;

/**
 * 文件工具类
 * @author jidonglin
 *
 */
public class FileUtils {
	public static boolean checkFileSize(MultipartFile file, int size, String unit) {
        long len = file.getSize();
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }
}
