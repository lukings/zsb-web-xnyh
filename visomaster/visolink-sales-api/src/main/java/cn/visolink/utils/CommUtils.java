package cn.visolink.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/9/4 7:28 下午
 */
public class CommUtils {
    public static Map buildTree(List<Map> menuList) {
        List<Map> trees = CollUtil.newArrayList();
        List<Map> childrenMaps =null;
        for (Map menu : menuList) {

            if ("-1".equals(menu.get("PID").toString())) {
                trees.add(menu);
            }
            childrenMaps= new ArrayList<>();
            for (Map it : menuList) {
                if (it.get("PID").equals(menu.get("ID"))) {
                    childrenMaps.add(it);
                }
            }
            menu.put("children",childrenMaps);

        }
        List<Object> featuresMenus = new ArrayList<>();
        if(!CollUtil.isEmpty(menuList)){
            for (Map featuresMenu : menuList) {
                String menusType = featuresMenu.get("menusType") + "";
                if("2".equals(menusType)){
                    featuresMenus.add(featuresMenu);
                }
            }
        }
        Map map = MapUtil.newHashMap();
        map.put("content",trees.size() == 0?menuList:trees);
        map.put("totalElements",menuList!=null?menuList.size():0);
        map.put("featuresMenus",featuresMenus);
        return map;
    }
    /**
     * @Author wanggang
     * @Description //APP 权限
     * @Date 18:59 2021/4/29
     * @Param [menuList]
     * @return java.util.Map
     **/
    public static Map buildTree1(List<Map> menuList) {
        List<Map> trees = CollUtil.newArrayList();
        List<Map> childrenMaps =null;
        for (Map menu : menuList) {

            if ("-1".equals(menu.get("PID").toString()) || "-2".equals(menu.get("PID").toString())) {
                trees.add(menu);
            }
            childrenMaps= new ArrayList<>();
            for (Map it : menuList) {
                if (it.get("PID").equals(menu.get("ID"))) {
                    childrenMaps.add(it);
                }
            }
            menu.put("children",childrenMaps);

        }
        List<Object> featuresMenus = new ArrayList<>();
        if(!CollUtil.isEmpty(menuList)){
            for (Map featuresMenu : menuList) {
                String menusType = featuresMenu.get("menusType") + "";
                if("2".equals(menusType)){
                    featuresMenus.add(featuresMenu);
                }
            }
        }
        Map map = MapUtil.newHashMap();
        map.put("content",trees.size() == 0?menuList:trees);
        map.put("totalElements",menuList!=null?menuList.size():0);
        map.put("featuresMenus",featuresMenus);
        return map;
    }
}
