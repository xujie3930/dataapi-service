package com.jinninghui.datasphere.icreditstudio.framework.result;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseComponent;
import com.jinninghui.datasphere.icreditstudio.framework.utils.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: Controller基类
 * @Author: hzh
 * @Date: 2021-2-20 8:13
 * @Version: 1.0
 */
@Slf4j
public class BaseController<T, S extends IService<T>> extends BaseComponent {

    @Autowired
    private S service;

    /**
     * 导出excel
     *
     * @param request
     */
    protected BusinessResult<?> exportExcel(HttpServletRequest request, HttpServletResponse response, T object, Class<T> clazz, String title) {

        try {
            // Step.1 组装查询条件
            QueryWrapper<T> queryWrapper = new QueryWrapper<T>(object);
            // Step.2 获取导出数据
            List<T> pageList = service.list(queryWrapper);
            List<T> exportList = null;
            // 可以传入需要导出的字段，columnFiledNamesList
            String columnFiledNames = request.getParameter("columnFiledNames");
            if (StringUtils.isNotEmpty(columnFiledNames)) {
                List<String> columnFiledNamesList = Arrays.asList(columnFiledNames.split(","));
                ExcelUtil.exportExcelColumnFiledNames(response, title, pageList, clazz, columnFiledNamesList);
                return BusinessResult.success("导出成功！");
            }
            // 过滤列表选中的数据 传入选中字段id
            String selections = request.getParameter("selections");
            if (StringUtils.isNotEmpty(selections)) {
                List<String> selectionList = Arrays.asList(selections.split(","));
                exportList = pageList.stream().filter(item -> selectionList.contains(getId(item))).collect(Collectors.toList());
            } else {
                exportList = pageList;
            }
            ExcelUtil.exportExcel(response, title, exportList, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BusinessResult.fail("", "文件导出失败:" + e.getMessage());
        }
        return BusinessResult.success("导出成功！");
    }


    /**
     * 获取对象ID
     *
     * @return
     */
    private String getId(T item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    protected BusinessResult<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<T> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            try {
                ExcelUtil.importExcel(file, new BaseDataListener(service), clazz);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return BusinessResult.fail("", "文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return BusinessResult.success("文件导入成功！");
    }


}
