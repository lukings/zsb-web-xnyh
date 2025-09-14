package cn.visolink.common.task;

import cn.hutool.core.util.StrUtil;
import cn.visolink.common.security.utils.DynamicDataSource;
import cn.visolink.message.dao.TimedTaskHandleMapper;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 多数据源定时任务处理类
 *
 * @author yangjie
 * @date 2020-9-8
 */
@Component
public class MultiDataSourceTimedTaskHandle {

    private final TimedTaskHandleMapper timedTaskHandleMapper;

    private final Executor executor = getThreadPool();

    @Value("${druidDataSource.Url}")
    private String druidDataSourceUrl;
    @Value("${druidDataSource.Username}")
    private String druidDataSourceUsername;
    @Value("${druidDataSource.Password}")
    private String druidDataSourcePassword;

    @Autowired
    public MultiDataSourceTimedTaskHandle(TimedTaskHandleMapper timedTaskHandleMapper) {
        this.timedTaskHandleMapper = timedTaskHandleMapper;
    }

    /**
     * 多数据源定时任务处理
     */
    public void taskHandle(FunInterface fun) {
        // 获取 数据源名称
        List<String> list = timedTaskHandleMapper.getDataBaseUrl();

        for (final String databaseName : list) {
            executor.execute(() -> {
                try {
                    System.out.println("====================:" + databaseName + "：开始");

                    // 切换数据源
                    DruidDataSource druidDataSource = new DruidDataSource();
                    druidDataSource.setUrl(StrUtil.format(druidDataSourceUrl, databaseName));
                    druidDataSource.setUsername(druidDataSourceUsername);
                    druidDataSource.setPassword(druidDataSourcePassword);
                    DynamicDataSource.dataSourcesMap.put(databaseName, druidDataSource);
                    DynamicDataSource.setDataSource(databaseName);

                    // 执行传递的函数
                    fun.fun();

                    System.out.println("====================:" + databaseName + "：完成");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("====================:" + databaseName + "：异常");
                } finally {
                    DynamicDataSource.clear();
                }
            });
        }
    }

    /**
     * 外部多数据源定时任务处理
     */
    public void externalTaskHandle(ExternalFunInterface fun) {
        // 获取 外部数据源
        List<Map<String, Object>> list = timedTaskHandleMapper.getExternalDataBaseUrl();

        for (final Map<String, Object> map : list) {
            String databaseName = map.get("DataBaseUrl").toString();
            executor.execute(() -> {
                try {
                    System.out.println("====================:" + databaseName + "：开始");

                    map.put("insideUrl", StrUtil.format(druidDataSourceUrl, databaseName));
                    map.put("insideUsername", druidDataSourceUsername);
                    map.put("insidePassword", druidDataSourcePassword);

                    // 执行传递的函数
                    fun.fun(map);

                    System.out.println("====================:" + databaseName + "：完成");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("====================:" + databaseName + "：异常");
                } finally {
                    DynamicDataSource.clear();
                }
            });
        }
    }

    /**
     * 创建线程池
     *
     * @return return
     */
    private Executor getThreadPool() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("timed-task-pool-%d").build();

        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), namedThreadFactory);
    }

    /**
     * 定时任务处理 函数式接口
     */
    public interface FunInterface {
        /**
         * 函数式接口 函数
         */
        void fun();
    }

    /**
     * 外部定时任务处理 函数式接口
     */
    public interface ExternalFunInterface {
        /**
         * 函数式接口 函数
         *
         * @param map map
         */
        void fun(Map<String, Object> map);
    }
}
