package cn.visolink.utils;

/**
 * @ClassName IdUtils
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/26 11:03
 **/
public class IdUtils {
    /**
     * 以毫微秒做基础计数, 返回唯一有序增长ID
     * <pre>System.nanoTime()</pre>
     * <pre>
     * 线程数量: 100
     * 执行次数: 1000
     * 平均耗时: 222 ms
     * 数组长度: 100000
     * Map Size: 100000
     * </pre>
     * @return ID长度10位
     */
    public static String getPrimaryKey(){
        return MathUtils.makeUpNewData(Thread.currentThread().hashCode()+"", 3)+ MathUtils.randomDigitNumber(7);           //随机7位数
    }

    public static void main(String[] args) {
        System.out.println("傅勇："+getPrimaryKey());
    }

}
