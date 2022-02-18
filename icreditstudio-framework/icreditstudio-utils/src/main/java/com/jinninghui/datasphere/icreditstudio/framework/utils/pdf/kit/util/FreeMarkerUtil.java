package com.jinninghui.datasphere.icreditstudio.framework.utils.pdf.kit.util;

import com.google.common.collect.Maps;
import com.jinninghui.datasphere.icreditstudio.framework.utils.pdf.kit.component.PDFKit;
import com.jinninghui.datasphere.icreditstudio.framework.utils.pdf.kit.exception.FreeMarkerException;
import com.jinninghui.datasphere.icreditstudio.framework.utils.pdf.kit.exception.PDFException;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by fgm on 2017/4/22.
 * FREEMARKER 模板工具类
 */
public class FreeMarkerUtil {

    private static final String WINDOWS_SPLIT = "\\";

    private static final String UTF_8="UTF-8";

    private static Map<String,FileTemplateLoader> fileTemplateLoaderCache=Maps.newConcurrentMap();

    private static  Map<String,Configuration> configurationCache= Maps.newConcurrentMap();

    public static Configuration getConfiguration(String templateFilePath){
        if(null!=configurationCache.get(templateFilePath)){
            return configurationCache.get(templateFilePath);
        }
        Configuration config = new Configuration(Configuration.VERSION_2_3_25);
        config.setDefaultEncoding(UTF_8);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        FileTemplateLoader fileTemplateLoader=null;
        if(null!=fileTemplateLoaderCache.get(templateFilePath)){
            fileTemplateLoader=fileTemplateLoaderCache.get(templateFilePath);
        }
        try {
            fileTemplateLoader=new FileTemplateLoader(new File(templateFilePath));
            fileTemplateLoaderCache.put(templateFilePath,fileTemplateLoader);
        } catch (IOException e) {
            throw new FreeMarkerException("fileTemplateLoader init error!",e);
        }
        config.setTemplateLoader(fileTemplateLoader);
        configurationCache.put(templateFilePath,config);
        return config;

    }


    /**
     * @description 根据模板名称获取模板,并且将数据构造具体的html字符串返回
     */
    public static String getContent(String templateName, Object data) {
        String templatePath = getPDFTemplatePathInClassPath(templateName);
        String templateFileName = getTemplateName(templatePath);
        String templateFilePath = getTemplatePath(templatePath);
        if (StringUtils.isEmpty(templatePath)) {
            throw new FreeMarkerException("templatePath can not be empty!");
        }
        try {
            Template template=getConfiguration(templateFilePath).getTemplate(templateFileName);
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            writer.flush();
            return  writer.toString();
        } catch (Exception ex) {
            throw new FreeMarkerException("FreeMarkerUtil process fail", ex);
        }
    }





    private static String getTemplatePath(String templatePath) {
        if (StringUtils.isEmpty(templatePath)) {
            return "";
        }
        if (templatePath.contains(WINDOWS_SPLIT)) {
            return templatePath.substring(0, templatePath.lastIndexOf(WINDOWS_SPLIT));
        }
        return templatePath.substring(0, templatePath.lastIndexOf("/"));
    }

    private static String getTemplateName(String templatePath) {
        if (StringUtils.isEmpty(templatePath)) {
            return "";
        }
        if (templatePath.contains(WINDOWS_SPLIT)) {
            return templatePath.substring(templatePath.lastIndexOf(WINDOWS_SPLIT) + 1);
        }
        return templatePath.substring(templatePath.lastIndexOf("/") + 1);
    }

    /**
     * @param path 在指定路径下查找pdf模板
     * @return 匹配到的模板名
     * @description 获取PDF的模板路径,
     * 默认按照PDF文件名匹对应模板
     */
    public static String getPDFTemplatePath(String path , String templateName) {
//        String templatePath = classpath + "/templates";
        File file = new File(path);
        if (!file.isDirectory()) {
            throw new PDFException("PDF模板文件不存在,请检查templates文件夹!");
        }
        File[] files = file.listFiles();
        for (File temp : files){
            if (temp.isDirectory()){
                String pdfTemplatePath = getPDFTemplatePath(temp.getAbsolutePath() , templateName);
                if (!StringUtils.isBlank(pdfTemplatePath)){
                    return pdfTemplatePath;
                }
                continue;
            }
            if (temp.getAbsoluteFile().getName().equals(templateName)){
                return temp.getAbsolutePath();
            }
        }

        return null;

    }

    /**
     * @param templateName 根据模板名称获取模板路径
     * @return 匹配到的模板名
     * @description 获取PDF的模板路径,
     * 默认按照PDF文件名匹对应模板
     */
    public static String getPDFTemplatePathInClassPath(String templateName) {
        String classpath = PDFKit.class.getClass().getResource("/").getPath();
        return getPDFTemplatePath(classpath , templateName);


    }


}
