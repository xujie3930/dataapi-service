package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.CallStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.druid.DataApiDruidDataSourceService;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.DatasourceFactory;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.DatasourceSync;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.DataApiGatewayPageResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.base.ApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.ResultSetToListUtils;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BusinessBasePageForm;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xujie
 * @description 数据源生成api
 * @create 2022-02-24 14:35
 **/
@Slf4j
@Service
public class GenerateService implements ApiBaseService {

    private static final String PAGENUM_MARK = "pageNum";
    private static final String PAGESIZE_MARK = "pageSize";
    private static final Integer PAGENUM_DEFALUT = 1;
    private static final Integer PAGESIZE_DEFALUT = 500;
    private static final Long RECORDS_MAX = 10000L;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public BusinessResult<Object> getData(Map params, RedisApiInfo apiInfo, ApiLogInfo apiLogInfo) throws SQLException {
        DatasourceSync factory = DatasourceFactory.getDatasource(apiInfo.getDatabaseType());
        String querySql = factory.parseSql(apiInfo.getQuerySql().replaceAll(";", ""), params);
        querySql = factory.getPageParamBySql(querySql, PAGENUM_DEFALUT, PAGESIZE_DEFALUT);
        log.error("数据源生成API查询sql：{}", querySql);
        try (Connection conn = DataApiDruidDataSourceService.getInstance()
                .getOrCreateConnection(apiInfo.getUrl(), apiInfo.getDatabaseType(), apiInfo.getUserName(), apiInfo.getPassword());
             Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            //连接数据源，执行SQL
            //如果传了分页参数要加上分页 并且返回的数据要用分页对象包装:BusinessResult<BusinessPageResult> ，分页的最大条数500
            if (params.containsKey(PAGENUM_MARK) && params.containsKey(PAGESIZE_MARK)) {
                Integer pageNum = Math.max(Integer.parseInt((String) params.get(PAGENUM_MARK)), PAGENUM_DEFALUT);
                Integer pageSize = Math.min(Integer.parseInt((String) params.get(PAGESIZE_MARK)), PAGESIZE_DEFALUT);
                querySql = factory.getPageParamBySql(querySql, pageNum, pageSize);
                DataApiGatewayPageResult<Object> build = getPageResult(pageNum, pageSize, querySql, apiLogInfo, stmt);
                return BusinessResult.success(build);
            } else {
                //如果不传分页最大查询500条，不需要用分页对象包装
                List list = getListResult(querySql, apiLogInfo, stmt);
                log.error("数据源生成API查询timestamp3：{}", System.currentTimeMillis());
                return BusinessResult.success(list);
            }

        } catch (Exception e) {
            log.error("连接数据源查询数据异常", e);
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000016.getCode(),
                    ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000016.getMessage());
        } finally {
        }
    }

    private DataApiGatewayPageResult<Object> getPageResult(Integer pageNum, Integer pageSize, String querySql, ApiLogInfo apiLogInfo, Statement stmt) throws SQLException {
        Long dataCount = 0L;
        String countSql = getSelectCountSql(querySql);
        ResultSet countRs = stmt.executeQuery(countSql);
        if (countRs.next()) {
            //rs结果集第一个参数即为记录数，且其结果集中只有一个参数
            dataCount = countRs.getLong(1);
        }
        log.info("查询sql分页:{}", querySql);
        ResultSet pagingRsForPageParam = stmt.executeQuery(querySql);
        ApiLogInfo successLog = generateSuccessLog(apiLogInfo, querySql);
        kafkaProducer.send(successLog);
        log.info("发送kafka成功日志:{}", successLog);
        if (pagingRsForPageParam.next()) {
            List list = ResultSetToListUtils.convertList(pagingRsForPageParam, apiLogInfo.getResponseParam());
            //发送成功消息
            BusinessBasePageForm pageForm = new BusinessBasePageForm();
            pageForm.setPageNum(pageNum);
            pageForm.setPageSize(pageSize);
            DataApiGatewayPageResult build = DataApiGatewayPageResult.build(list, pageForm, dataCount);
            build.setPageCount(Math.min(build.getPageCount(), RECORDS_MAX % pageSize == 0? RECORDS_MAX / pageSize : RECORDS_MAX / pageSize + 1));
            return build;
        }
        return null;
    }

    private String getSelectCountSql(String sql) {
        String matterSelect = "select";
        String matterFrom = "from";
        String countSsql = " count(*) ";
        String orderBy = "order by";
        String groupBy = "group by";
        int selectIndex = sql.indexOf(matterSelect);//第一个字符串的起始位置
        int limitIndex = sql.indexOf("limit");//第一个字符串的起始位置
        int fromIndex = sql.indexOf(matterFrom);//第二个字符串的起始位置
        sql = sql.substring(0, selectIndex + matterSelect.length()) + countSsql + sql.substring(fromIndex, limitIndex);
        if (sql.contains(orderBy)){
            sql = sql.substring(0, sql.indexOf(orderBy));
        }
        if (sql.contains(groupBy)){
            sql = sql.substring(0, sql.indexOf(groupBy));
        }
        return sql;
    }

    private List getListResult(String querySql, ApiLogInfo apiLogInfo, Statement stmt) throws SQLException {
        log.info("查询sql:{}", querySql);
        ResultSet pagingRs = stmt.executeQuery(querySql);
        //发送成功消息
        ApiLogInfo successLog = generateSuccessLog(apiLogInfo, querySql);
        kafkaProducer.send(successLog);
        log.info("发送kafka成功日志:{}", successLog);
        if (pagingRs.next()) {
            List list = ResultSetToListUtils.convertList(pagingRs, apiLogInfo.getResponseParam());
            return list;
        }
        return null;
    }

    private ApiLogInfo generateSuccessLog(ApiLogInfo apiLogInfo, String querySql) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_SUCCESS.getCode());
        apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        //取where条件后面的值,取到limit
        if (querySql.contains("where")){
            String requestParam = StringUtils.splitBetween(querySql, "where", "limit").trim();
            requestParam = requestParam.replaceAll("and ", ",");
            requestParam = requestParam.replaceAll("1=1 ,", "").trim();
            apiLogInfo.setRequestParam(requestParam);
        }else {
            apiLogInfo.setRequestParam(null);
        }
        return apiLogInfo;
    }
}
