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
public class ConnectionManagerXZS {
    private static String XZSurl;
    private static String XZSuser;
    private static String XZSpassword;

    @Value("${XZSurl}")
    public void setRurl(String XZSurl) {
        ConnectionManagerXZS.XZSurl = XZSurl;
    }
    @Value("${XZSuser}")
    public void setDBuser(String XZSuser) {
        ConnectionManagerXZS.XZSuser = XZSuser;
    }
    @Value("${XZSpassword}")
    public void setDBpassword(String XZSpassword) {
        ConnectionManagerXZS.XZSpassword = XZSpassword;
    }

    private static ComboPooledDataSource DataSource;

    private static ConnectionManagerXZS instance;

    private ConnectionManagerXZS() {
    }

    public static ConnectionManagerXZS getInstance(){
        if (instance == null) {
            synchronized (ConnectionManagerXZS.class) {
             //   if (instance == null) {
                    try {
                        initPool();
                        instance = new ConnectionManagerXZS();
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    }
               // }
            }
        }
        return instance;
    }

    private static ComboPooledDataSource getDataSource(String url) throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(XZSuser);
        dataSource.setPassword(XZSpassword);
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
        dataSource.setInitialPoolSize(10);
        dataSource.setMinPoolSize(10);
        int maxPoolSize = (XZSurl.equals( url)) ? 100 : 50;
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
        DataSource = getDataSource(XZSurl);
    }

    public Connection getConnection() {
        Connection conn = null;
        if (null != DataSource) {
            try {
                conn = DataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }



    public DataSource getDatasource() {
        return DataSource;
    }

    /**
     * 获取数据库连接池中的总连接数，忙连接数量，空闲连接数量，未关闭连接数量
     * @return 读连接池和写连接池连接信息
     */
    public String showConnPoolInfo(){
        return "读连接池信息：" + getDataSourceInfo(DataSource) + "，写连接池信息：" + getDataSourceInfo(DataSource);
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