package cn.visolink.system.card.mapper;

import cn.visolink.system.card.model.UserCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
@Mapper
public interface UserCardMapper {

    /**
     * 新增名片信息
     *
     * @param userCard 名片信息
     * @return 结果
     */
    int addCardInfo(UserCard userCard);

    /**
     * 根据用户ID查询名片详情
     *
     * @param userId 用户ID
     * @return 名片详情
     */
    UserCard getCardInfoByUserId(@Param("userId") String userId);

    /**
     * 查询用户基本信息
     *
     * @param userId 用户ID
     * @return 名片详情
     */
    Map<String, Object> getUserBaseInfo(@Param("userId") String userId);

    /**
     * 删除名片推荐楼盘数据
     *
     * @param map
     * @return
     */
    int delCardBuildBook(Map<String, Object> map);

}
