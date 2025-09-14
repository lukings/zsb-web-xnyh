package cn.visolink.common.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import io.lettuce.core.KeyScanCursor;

import java.time.Duration;
import java.util.List;

public class RedisBatchDelete {

    public static void main(String[] args) {
        // 配置信息
        String host = "192.168.47.64";
        int port = 54318;
        String password = "wyjt@2024";
        //1-shi app  2是PC
        //int database = 2;//1-viso.VISO.User.info.* TokenKey.*
        int database = 1;//1-viso.VISO.User.info.*   TokenKey.* .*  outbound.*
        int timeout = 20000;
        int maxActive = 500;
        int maxIdle = 10;
        int minIdle = 0;
        long maxWait = -1;

        // 创建 Redis URI
        RedisURI redisURI = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withPassword(password.toCharArray())
                .withDatabase(database)
                .withTimeout(Duration.ofMillis(timeout))
                .build();

        // 创建连接池配置
        GenericObjectPoolConfig<StatefulRedisConnection<String, String>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);

        // 创建 Redis 客户端和连接池
        RedisClient redisClient = RedisClient.create(redisURI);
        GenericObjectPool<StatefulRedisConnection<String, String>> pool =
                ConnectionPoolSupport.createGenericObjectPool(redisClient::connect, poolConfig);

        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            RedisCommands<String, String> commands = connection.sync();

            // 扫描并批量删除以 "TokenKey.liujin" 开头的键
           //String pattern = "TokenKey.*";
            String pattern = "outbound.*";

            ScanArgs scanArgs = new ScanArgs().match(pattern).limit(1000);
            ScanCursor cursor = ScanCursor.INITIAL;

            int totalDeleted = 0;
            int batchCount = 0;
            long startTime = System.currentTimeMillis();

            System.out.println("开始扫描和删除匹配的键...");

            do {
                batchCount++;
                KeyScanCursor<String> scanResult = commands.scan(cursor, scanArgs);
                List<String> keys = scanResult.getKeys();
                
                System.out.printf("第 %d 批次：扫描到 %d 个键\n", batchCount, keys.size());
                
                if (!keys.isEmpty()) {
                    Long deleted = commands.del(keys.toArray(new String[0]));
                    int deletedCount = deleted != null ? deleted.intValue() : 0;
                    totalDeleted += deletedCount;
                    System.out.printf("第 %d 批次：删除 %d 个键，累计删除 %d 个\n", 
                                     batchCount, deletedCount, totalDeleted);
                } else {
                    System.out.printf("第 %d 批次：无匹配键\n", batchCount);
                }
                
                cursor = ScanCursor.of(scanResult.getCursor());
            } while (!cursor.isFinished());

            long endTime = System.currentTimeMillis();
            System.out.printf("批量删除完成！共删除 %d 个键，耗时 %d 毫秒\n", totalDeleted, (endTime - startTime));

        } catch (Exception e) {
            System.err.println("执行批量删除时发生错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (pool != null) {
                pool.close();
            }
            if (redisClient != null) {
                redisClient.shutdown();
            }
        }
    }
}