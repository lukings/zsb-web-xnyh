package cn.visolink.system.card.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.card.mapper.UserCardMapper;
import cn.visolink.system.card.model.UserCard;
import cn.visolink.system.card.service.UserCardService;
import cn.visolink.utils.StringUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * @author hjb
 */
@Service
public class UserCardServiceImpl implements UserCardService {

    @Autowired
    private UserCardMapper userCardMapper;

    /**
     * 维护名片数据
     *
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateCardData(Map<String, Object> map) {
        String accountId = MapUtils.getString(map, "accountId", "");
        if (StringUtils.isEmpty(accountId)) {
            accountId = MapUtils.getString(map, "ID", "");
        }
        // 根据用户ID查询名片数据
        UserCard card = userCardMapper.getCardInfoByUserId(accountId);
        if (card == null) {
            Map<String, Object> userBaseInfo = userCardMapper.getUserBaseInfo(accountId);
            UserCard userCard = BeanUtil.mapToBeanIgnoreCase(userBaseInfo, UserCard.class, true);
            userCard.setId(UUID.randomUUID().toString());
            // 新增名片数据
            userCardMapper.addCardInfo(userCard);
        }
        return ResultBody.success("操作成功！");
    }

    /**
     * 删除名片楼盘排序数据
     *
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody delCardBuildBook(Map<String, Object> map) {
        userCardMapper.delCardBuildBook(map);
        return ResultBody.success("操作成功！");
    }

}
