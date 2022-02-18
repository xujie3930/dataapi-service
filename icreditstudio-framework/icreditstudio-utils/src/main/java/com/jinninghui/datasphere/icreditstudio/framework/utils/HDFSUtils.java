package com.jinninghui.datasphere.icreditstudio.framework.utils;

/**
 * @author xujie
 * @description hdfs工具类
 * @create 2021-08-27 14:13
 **/

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;

@Slf4j
public class HDFSUtils {
    //TODO:这里改为配置文件配置，，同时引入hadoop-hdfs的pom版本得和hadoop安装的版本一致
    public static String HDFS_URL = "hdfs://192.168.0.174:8020";
    static Configuration conf = new Configuration();
    static FileSystem fs;
    static {
        conf.set("fs.defaultFS", HDFS_URL);
        //hadoop的hdfs-site.xml文件,也要配置该impl参数
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        try {
            fs = FileSystem.get(URI.create(HDFSUtils.HDFS_URL),conf,"root");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String copyStringToHDFS(String str, String destPath){

        InputStream fis = new ByteArrayInputStream(str.getBytes());
        String storePath = "/datasource/" + destPath + ".txt";
        OutputStream os = null;
        try {
            os = fs.create(new Path(storePath));
            IOUtils.copyBytes(fis, os, 4096, true);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != os){
                    os.close();
                }
                if(null != fis){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return storePath;
    }

    public static String getStringFromHDFS(String destPath){

        StringBuffer stringBuffer = new StringBuffer();
        FSDataInputStream in = null;
        BufferedReader bufferedReader = null;
        try {
            in = fs.open(new Path(destPath));
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String lineTxt ;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                stringBuffer.append(lineTxt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != bufferedReader){
                    bufferedReader.close();
                }
                if(null != in){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }

    public static void delFileFromHDFS(String delPath){
        try {
            fs.delete(new Path(delPath),true);
        } catch (IOException e) {
            log.error("文件:{}删除失败", delPath);
        }
    }

    public static void main(String[] args) throws Exception {
        /*String stringFromHDFS = getStringFromHDFS("/datasource/891684917153370112.txt");
        System.out.println(stringFromHDFS.length());
        System.out.println(stringFromHDFS);*/
        delFileFromHDFS("/datasource/891684917153370112.txt");
    }

}
