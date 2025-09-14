package cn.visolink.system.commission.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommissionMapper {


    /**
     * 获取无效数据列表
     *
     * @return
     */
    List<Map<String, Object>> getInvalidDataList(Map<String, Object> map);
}
