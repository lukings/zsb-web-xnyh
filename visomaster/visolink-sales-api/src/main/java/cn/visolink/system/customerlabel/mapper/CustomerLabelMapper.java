package cn.visolink.system.customerlabel.mapper;

import cn.visolink.system.customerlabel.entity.CustomerLabel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 个人标签信息表 Mapper 接口
 *
 * @author system
 * @since 2024-01-01
 */
@Mapper
public interface CustomerLabelMapper extends BaseMapper<CustomerLabel> {

    /**
     * 根据创建人查询标签列表
     *
     * @param creator 创建人
     * @return 标签列表
     */
    List<CustomerLabel> selectByCreator(@Param("creator") String creator);
}
