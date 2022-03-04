package com.jinninghui.datasphere.icreditstudio.dataapi.utils;

/**
 * @author xujie
 * @description 动态创建数据源连接池
 * @create 2022-01-28 17:33
 **/

import com.jinninghui.datasphere.icreditstudio.dataapi.common.DatasourceTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

@Slf4j
public class DBConnectionManager {
    static private DBConnectionManager instance;
    static private int clients;
    private final Vector drivers = new Vector();
    private Hashtable pools = new Hashtable();
    //cpu核心数*2
    private final int defaultConn = 10;

    public Hashtable getPools() {
        return pools;
    }

    private DBConnectionManager() {
    }

    static synchronized public DBConnectionManager getInstance() {
        if (instance == null) {
            instance = new DBConnectionManager();
        }
        clients++;
        return instance;
    }

    public String createPools(String url, String username, String password, Integer type) {
        String poolName = url;
        String drvier = getDrvierByType(type);
        DBConnectionPool pool = new DBConnectionPool(poolName, drvier, url, username, password, defaultConn);
        pools.put(poolName, pool);
        return poolName;
    }

    static String getDrvierByType(Integer type) {
        String driver = DatasourceTypeEnum.findDatasourceTypeByType(type).getDriver();
        return driver;
    }


    public void freeConnection(String url, Connection conn) {
        if (conn == null){
            return;
        }
        DBConnectionPool pool = (DBConnectionPool) pools.get(url);
        if (pool != null) {
            pool.freeConnection(conn);
        }
    }

    /**
     * @param url
     * @param username
     * @param password
     * @param type
     * @return
     */
    public Connection getConnection(String url, String username, String password, Integer type) {
        DBConnectionPool pool = (DBConnectionPool) pools.get(url);
        if (pool == null) {
            createPools(url, username, password, type);
            pool = (DBConnectionPool) pools.get(url);
        }
        return pool.getConnection();
    }


    public synchronized void release() {
        if (--clients != 0) {
            return;
        }

        Enumeration allPools = pools.elements();
        while (allPools.hasMoreElements()) {
            DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
            pool.release();
        }

        Enumeration allDrivers = drivers.elements();
        while (allDrivers.hasMoreElements()) {
            Driver driver = (Driver) allDrivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
            }
        }
    }


    public class DBConnectionPool {
        private final Vector freeConnections = new Vector();
        private final int maxConn;
        private final String name;
        private final String driver;
        private final String password;
        private final String URL;
        private final String user;
        private int checkedOut;

        public DBConnectionPool(String name, String driver, String URL, String user, String password, int maxConn) {
            this.name = name;
            this.driver = driver;
            this.URL = URL;
            this.user = user;
            this.password = password;
            this.maxConn = maxConn;
        }

        public synchronized void freeConnection(Connection con) {
            freeConnections.add(con);
            checkedOut--;
            notifyAll();
        }

        public synchronized Connection getConnection() {
            Connection con = null;
            if (freeConnections.size() > 0) {
                con = (Connection) freeConnections.firstElement();
                freeConnections.removeElementAt(0);
                try {
                    if (con.isClosed()) {
                        con = getConnection();
                    }
                } catch (SQLException e) {
                    con = getConnection();
                }
            } else if (maxConn == 0 || checkedOut < maxConn) {
                con = newConnection();
            }
            if (con != null) {
                checkedOut++;
            }
            return con;
        }

        public synchronized void release() {
            Enumeration allConnections = freeConnections.elements();
            while (allConnections.hasMoreElements()) {
                Connection con = (Connection) allConnections.nextElement();
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
            freeConnections.removeAllElements();
        }

        private Connection newConnection() {
            Connection con = null;
            try {
                if (user == null) {
                    con = DriverManager.getConnection(URL);
                } else {
                    con = DriverManager.getConnection(URL, user, password);
                }
            } catch (SQLException e) {
                return null;
            }
            return con;
        }
    }
}
