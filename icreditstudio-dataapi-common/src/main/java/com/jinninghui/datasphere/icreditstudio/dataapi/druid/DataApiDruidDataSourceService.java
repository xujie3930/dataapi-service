package com.jinninghui.datasphere.icreditstudio.dataapi.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DatasourceTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.druid
 * ClassName: DataApiDruidDataSourceHelper
 * Description:  DataApiDruidDataSourceHelper类
 * Date: 2022/5/11 12:24
 *
 * @author liyanhui
 */
public final class DataApiDruidDataSourceService {

    /**
     * key : jdbc url
     * value: datasource instance
     */
    public Map<String, DataApiDruidDataSource> map = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(DataApiDruidDataSourceService.class);
    static private DataApiDruidDataSourceService instance;

    private DataApiDruidDataSourceService() {
    }

    static synchronized public DataApiDruidDataSourceService getInstance() {
        if (instance == null) {
            instance = new DataApiDruidDataSourceService();
        }
        return instance;
    }


    /**
     * 用配置项代替
     *
     * @param url
     * @param type
     * @param userName
     * @param password
     * @return
     * @throws SQLException
     */
    DataApiDruidDataSource initDataSource(String url, Integer type, String userName, String password) throws Exception {
        Properties prop = new Properties();
        prop.setProperty("driver", DatasourceTypeEnum.findDatasourceTypeByType(type).getDriver());
        prop.setProperty("url", url);
        prop.setProperty("connectionProperties", "useUnicode=true;characterEncoding=UTF8");
        prop.setProperty("username", userName);
        prop.setProperty("password", password);
        // 设置数据连接池初始化连接池数量
        prop.setProperty("initialSize", "3");
        // 最大连接数
        prop.setProperty("maxActive", "50");
        prop.setProperty("minIdle", "3");
        // 连接最大等待时间60秒
        prop.setProperty("maxWait", "60000");
        prop.setProperty("filters", "stat");
        prop.setProperty("timeBetweenEvictionRunsMillis", "35000");
        prop.setProperty("minEvictableIdleTimeMillis", "30000");
        prop.setProperty("testWhileIdle", "true");
        prop.setProperty("testOnBorrow", "false");
        prop.setProperty("testOnReturn", "false");
        prop.setProperty("poolPreparedStatements", "false");
        prop.setProperty("maxPoolPreparedStatementPerConnectionSize", "200");
        prop.setProperty("removeAbandoned", "true");
        try {
            DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(prop);
            DataApiDruidDataSource dataApiDruidDataSource = new DataApiDruidDataSource(druidDataSource);
            Date now = new Date();
            dataApiDruidDataSource.setCreateDate(now);
            dataApiDruidDataSource.setLastUseDate(now);
            map.put(url, dataApiDruidDataSource);
            return dataApiDruidDataSource;
        } catch (Exception e) {
            logger.error("初始化创建数据源{}连接池失败！", url, e);
            throw e;
        }
    }

    public DruidPooledConnection getOrCreateConnection(String url, Integer type, String userName, String password) throws Exception {
        DataApiDruidDataSource source;
        synchronized (this) {
            if (!map.containsKey(url)) {
                source = initDataSource(url, type, userName, password);
                return (DruidPooledConnection) source.getDruidDataSource().getPooledConnection();
            }
        }
        source = map.get(url);
        source.setLastUseDate(new Date());
        logger.warn("当前数据库连接池的量为：" + source.getDruidDataSource().getActiveConnections().size() + "---" + source.getDruidDataSource().getActiveCount() + "---" + source.getDruidDataSource().getCloseCount());
        return (DruidPooledConnection) source.getDruidDataSource().getPooledConnection();
    }

}
