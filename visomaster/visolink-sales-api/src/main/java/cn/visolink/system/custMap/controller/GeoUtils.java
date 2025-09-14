package cn.visolink.system.custMap.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoUtils {

    /**
     * 判断一个经纬度点是否在给定的经纬度区域内
     * @param latLonStr 经纬度区域字符串，格式如："[{\"lng\":113.655672,\"lat\":34.810713},...]"
     * @param targetLng 待判断点的经度
     * @param targetLat 待判断点的纬度
     * @return 1表示在区域内，0表示在区域外
     */
    public static int checkPointInArea(String latLonStr, double targetLng, double targetLat) {
        // 参数校验
        if (latLonStr == null || latLonStr.trim().isEmpty()) {
            return 0;
        }

        // 从字符串中提取经纬度点
        List<Point> points = parseLatLonString(latLonStr);

        // 如果区域点少于3个，无法构成有效区域
        if (points.size() < 3) {
            return 0;
        }

        // 使用射线法判断点是否在多边形内
        boolean inside = isPointInPolygon(targetLng, targetLat, points);

        return inside ? 1 : 0;
    }

    /**
     * 解析经纬度字符串，提取所有经纬度点
     */
    private static List<Point> parseLatLonString(String latLonStr) {
        List<Point> points = new ArrayList<>();

        // 使用正则表达式提取经纬度，支持负数（西经、南纬）
        Pattern pattern = Pattern.compile("\"lng\":([\\-\\d.]+),\"lat\":([\\-\\d.]+)");
        Matcher matcher = pattern.matcher(latLonStr);

        while (matcher.find()) {
            try {
                double lng = Double.parseDouble(matcher.group(1));
                double lat = Double.parseDouble(matcher.group(2));
                points.add(new Point(lng, lat));
            } catch (NumberFormatException e) {
                // 忽略解析失败的坐标点
                continue;
            }
        }

        return points;
    }

    /**
     * 使用射线法判断点是否在多边形内
     */
    private static boolean isPointInPolygon(double x, double y, List<Point> polygon) {
        boolean inside = false;
        int n = polygon.size();
        
        for (int i = 0, j = n - 1; i < n; j = i++) {
            Point pi = polygon.get(i);
            Point pj = polygon.get(j);
            
            // 检查点是否在多边形的边上
            if (isPointOnLine(x, y, pi, pj)) {
                return true;
            }
            
            // 射线法核心逻辑
            if (((pi.lat > y) != (pj.lat > y)) &&
                (x < (pj.lng - pi.lng) * (y - pi.lat) / (pj.lat - pi.lat + 1e-12) + pi.lng)) {
                inside = !inside;
            }
        }
        
        return inside;
    }

    /**
     * 判断点是否在线段上
     */
    private static boolean isPointOnLine(double x, double y, Point a, Point b) {
        // 使用叉积判断点是否在线段上
        double cross = (b.lng - a.lng) * (y - a.lat) - (b.lat - a.lat) * (x - a.lng);
        if (Math.abs(cross) > 1e-8) {
            return false;
        }
        
        // 使用点积判断点是否在线段范围内
        double dot = (x - a.lng) * (x - b.lng) + (y - a.lat) * (y - b.lat);
        return dot <= 0;
    }

    /**
     * 表示经纬度点的内部类
     */
    private static class Point {
        double lng;
        double lat;

        public Point(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }
    }
}
