package cn.visolink.system.card.service;

import cn.visolink.exception.ResultBody;

import java.util.Map;

public interface UserCardService {

    /**
     * 维护名片数据
     *
     * @param map
     * @return
     */
    ResultBody updateCardData(Map<String, Object> map);

    /**
     * 删除名片楼盘排序数据
     *
     * @param map
     * @return
     */
    ResultBody delCardBuildBook(Map<String, Object> map);


}
