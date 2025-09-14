package cn.visolink.utils;

import org.apache.lucene.util.RamUsageEstimator;

public class SizeUtils {

    public static String getObjectSize(Object o) {
        return RamUsageEstimator.humanSizeOf(o);
    }
}
