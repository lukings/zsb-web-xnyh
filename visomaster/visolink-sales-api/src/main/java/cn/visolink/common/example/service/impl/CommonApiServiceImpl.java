package cn.visolink.common.example.service.impl;

import cn.visolink.common.example.mapper.CommonApiMapper;
import cn.visolink.common.example.model.DataStatus;
import cn.visolink.common.example.model.Dict;
import cn.visolink.common.example.model.UserOrg;
import cn.visolink.common.example.service.CommonApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author YHX
 * @date 2021年11月23日 16:13
 */
@Service
public class CommonApiServiceImpl implements CommonApiService {

    @Autowired
    private CommonApiMapper commonApiMapper;


    /**
     * dictCode 字典编码
     * projectId 项目ID
     * 查询字典列表
     *
     * @param dictCode  字典Code
     * @param projectId 项目id
     * @return list
     * */
    @Override
    public List<Dict> getCommonDictList(String dictCode, String projectId, String cityId) {
        return commonApiMapper.getCommonDictList(dictCode,projectId,cityId);
    }

    /**
     * 变更数据状态
     *
     * @param dataStatus 数据状态
     * @return 条数
     * */
    @Override
    public int updateDataStatus(DataStatus dataStatus) {
        return commonApiMapper.updateDataStatus(dataStatus);
    }

    /**
     * 获取登录人所属组织
     *
     * @param userId
     * @param type
     * @return
     */
    @Override
    public List<UserOrg> getUserOrgList(String userId, Integer type) {
        return commonApiMapper.getUserOrgList(userId,type);
    }
}
