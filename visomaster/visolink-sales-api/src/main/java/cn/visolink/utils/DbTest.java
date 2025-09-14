package cn.visolink.utils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @Auther: wang gang
 * @Date: 2019/4/28 15:02
 */
@Component
public class DbTest {
    /**
     * 查询数据多条
     * @param tableName
     * @param wheres
     * @param whereValue
     * @return
     */
    public static List<Map<String,Object>> getObjects(String tableName,String wheres,String whereValue){
        List<Map<String,Object>> map = new ArrayList<>();
        Connection connection = ConnectionManager.getInstance().getRConnection();
        try{
            String sql = "";
            if (wheres!=null && !"".equals(wheres) && whereValue!=null && !"".equals(whereValue)){
                sql = "select * from "+tableName+" (NOLOCK) where "+wheres+"='"+whereValue+"'";
            }else{
                sql = "select * from "+tableName;
            }
            map = new QueryRunner().query(connection,
                    sql, new MapListHandler());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    /**
     * 查询数据多条
     * @param sql
     * @return
     */
    public static List<Map<String,Object>> getObjects(String sql){
        List<Map<String,Object>> map = new ArrayList<>();
        Connection connection = ConnectionManager.getInstance().getRConnection();
        try{
            map = new QueryRunner().query(connection,sql, new MapListHandler());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }


    /**
     * 查询数据单条
     * @param tableName
     * @param wheres
     * @param whereValue
     * @return
     */
    public static Map<String,Object> getObject(String tableName,String wheres,String whereValue){
        Map<String,Object> map = new HashMap<>();
        Connection connection = ConnectionManager.getInstance().getRConnection();
        try{
            String sql = "";
            sql = "select * from "+tableName+" (NOLOCK) where "+wheres+" = '"+whereValue+"'";
            map = new QueryRunner().query(connection,sql, new MapHandler());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 查询数据单条
     * @param sql
     * @return
     */
    public static Map<String,Object> getObject(String sql){
        Map<String,Object> map = new HashMap<>();
        Connection connection = ConnectionManager.getInstance().getRConnection();
        try{
            map = new QueryRunner().query(connection,
                    sql, new MapHandler());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 保存方法，注意这里传递的是实际的表的名称
     */
    public static int saveObject(String tableName, Map<String,String> map){
        int re = 0;
        Connection connection = ConnectionManager.getInstance().getWConnection();
        try{
            String sql = " insert into " + tableName + " (";
            Set<String> set = map.keySet();
            for(String key : set){
                sql += (key + ",");
            }
            sql = sql.substring(0,sql.length()-1);
            sql += " ) ";
            sql += " values ( ";
            for(String key : set){
                if (map.get(key)==null || "".equals(map.get(key))){
                    sql += ( null + ",");
                }else{
                    sql += ("'" + map.get(key) + "',");
                }
            }
            sql = sql.substring(0,sql.length()-1);
            sql += (" ) ");

            re = new QueryRunner().update(connection,sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return re;
    }

    /**
     * 更新表数据方法
     */
    public static int updateObject(String tableName, Map<String,String> map, Map<String,String> whereMap) throws Exception{
        int re = 0;
        Connection connection = ConnectionManager.getInstance().getWConnection();
        try{
            String sql = " update " + tableName + " set ";
            Set<String> set = map.keySet();
            for(String key : set){
                if (map.get(key)==null || "".equals(map.get(key))){
                    sql += (key + "= null,");
                }else{
                    sql += (key + "= '"+map.get(key)+"',");
                }
            }
            sql = sql.substring(0,sql.length()-1);
            sql += " where 1 = 1";
            Set<String> set1 = whereMap.keySet();
            for(String key : set1){
                sql += (" and "+key + "= '"+whereMap.get(key)+"'");
            }

            re = new QueryRunner().update(connection,sql);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return re;
    }
    /**
     * 更新表数据方法
     */
    public static int updateObject(String sql) throws Exception{
        int re = 0;
        Connection connection = ConnectionManager.getInstance().getWConnection();
        try{
            re = new QueryRunner().update(connection,sql);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return re;
    }

}
