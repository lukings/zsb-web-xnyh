package cn.visolink.system.phone.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/12/21 13:36
 */
@Mapper
public interface PhoneMapper {


    /**
     * 获取认知渠道
     * */
    List<Map> getParentChannel();

    /**
    * 获取认知通道
    * */
    List<Map> getChildChannel(@Param("projectList") List projectList);
}


