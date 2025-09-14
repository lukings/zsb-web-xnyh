package cn.visolink.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ConnectionManager {
    private static String rurl;
    private static String wurl;
    private static String DBuser;
    private static String DBpassword;

    @Value("${DBDurl}")
    public void setRurl(String rurl) {
        ConnectionManager.rurl = rurl;
    }
    @Value("${DBXurl}")
    public void setXurl(String wurl) {
        ConnectionManager.wurl = wurl;
    }
    @Value("${DBuser}")
    public void setDBuser(String DBuser) {
        ConnectionManager.DBuser = DBuser;
    }
    @Value("${DBpassword}")
    public void setDBpassword(String DBpassword) {
        ConnectionManager.DBpassword = DBpassword;
    }

    private static ComboPooledDataSource rDataSource;
    private static ComboPooledDataSource wDataSource;

    private static ConnectionManager instance;

    private ConnectionManager() {
    }

    public static ConnectionManager getInstance(){
        if (instance == null) {
            synchronized (ConnectionManager.class) {
               // if (instance == null) {
                    try {
                        initPool();
                        instance = new ConnectionManager();
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    }
                //}
            }
        }
        return instance;
    }

    private static ComboPooledDataSource getDataSource(String url) throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(DBuser);
        dataSource.setPassword(DBpassword);
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setInitialPoolSize(10);
        dataSource.setMinPoolSize(10);
        int maxPoolSize = (rurl.equals( url)) ? 100 : 50;
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setMaxStatements(100);
        dataSource.setMaxIdleTime(60);
        //xinzeng peizhi
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setIdleConnectionTestPeriod(20);

        return dataSource;
    }

    /**
     * @return void
     * @Description 初始化连接池，使池中的连接数达到最小值
     */
    private static void initPool() throws PropertyVetoException {
        rDataSource = getDataSource(rurl);
        wDataSource = getDataSource(wurl);
    }

    public Connection getRConnection() {
        Connection conn = null;
        if (null != rDataSource) {
            try {
                conn = rDataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public Connection getWConnection() {
        Connection conn = null;
        if (null != wDataSource) {
            try {
                conn = wDataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public DataSource getRDatasource() {
        return rDataSource;
    }

    public DataSource getWDatasource() {
        return wDataSource;
    }

    /**
     * 获取数据库连接池中的总连接数，忙连接数量，空闲连接数量，未关闭连接数量
     * @return 读连接池和写连接池连接信息
     */
    public String showConnPoolInfo(){
        return "读连接池信息：" + getDataSourceInfo(rDataSource) + "，写连接池信息：" + getDataSourceInfo(wDataSource);
    }

    /**
     * 获取数据库连接池中的总连接数，忙连接数量，空闲连接数量，未关闭连接数量
     * @param dataSource 连接池信息
     * @return 连接池中的总连接数，忙连接数量，空闲连接数量，未关闭连接数量
     */
    private String getDataSourceInfo(ComboPooledDataSource dataSource){
        PooledDataSource pds = (PooledDataSource) dataSource;
        String result = "";
        try {
            result =  "[总连接数量：" + pds.getNumConnectionsDefaultUser()
                    + ",忙连接数量："  + pds.getNumBusyConnectionsDefaultUser()
                    + ",空闲连接数量：" + pds.getNumIdleConnectionsDefaultUser()
                    + ",未关闭连接数量：" + pds.getNumUnclosedOrphanedConnectionsAllUsers() + "]";
        } catch (SQLException e) {
            System.out.println("c3p0连接池异常！");
        }
        return  result;
    }
} 