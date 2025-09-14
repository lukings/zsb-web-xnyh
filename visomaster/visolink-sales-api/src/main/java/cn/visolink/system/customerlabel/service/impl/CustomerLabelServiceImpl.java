package cn.visolink.system.customerlabel.service.impl;

import cn.visolink.system.customerlabel.entity.CustomerLabel;
import cn.visolink.system.customerlabel.mapper.CustomerLabelMapper;
import cn.visolink.system.customerlabel.service.ICustomerLabelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 个人标签信息表 服务实现类
 *
 * @author system
 * @since 2024-01-01
 */
@Service
public class CustomerLabelServiceImpl extends ServiceImpl<CustomerLabelMapper, CustomerLabel> implements ICustomerLabelService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCustomerLabel(CustomerLabel customerLabel, String creator) {
        // 手动生成UUID
        customerLabel.setId(UUID.randomUUID().toString());
        
        // 设置创建信息
        customerLabel.setCreator(creator);
        customerLabel.setCreateTime(new Date());
        customerLabel.setIsDel(0);
        
        return save(customerLabel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddCustomerLabels(List<CustomerLabel> customerLabels, String creator) {
        if (CollectionUtils.isEmpty(customerLabels)) {
            return 0;
        }

        // 批量设置创建信息
        for (CustomerLabel customerLabel : customerLabels) {
            customerLabel.setId(UUID.randomUUID().toString());
            customerLabel.setCreator(creator);
            customerLabel.setCreateTime(new Date());
            customerLabel.setIsDel(0);
        }

        // 批量保存
        return saveBatch(customerLabels) ? customerLabels.size() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustomerLabel(String id, String editor) {
        CustomerLabel customerLabel = getById(id);
        if (customerLabel == null) {
            return false;
        }
        
        // 逻辑删除
        customerLabel.setIsDel(1);
        customerLabel.setEditor(editor);
        customerLabel.setEditTime(new Date());
        
        return updateById(customerLabel);
    }

    @Override
    public List<CustomerLabel> getCustomerLabelsByCreator(String creator) {
        return baseMapper.selectByCreator(creator);
    }
}
