package cn.visolink.system.customerlabel.service;

import cn.visolink.system.customerlabel.entity.CustomerLabel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 个人标签信息表 服务类
 *
 * @author system
 * @since 2024-01-01
 */
public interface ICustomerLabelService extends IService<CustomerLabel> {

    /**
     * 新增标签
     *
     * @param customerLabel 标签信息
     * @param creator 创建人
     * @return 是否成功
     */
    boolean addCustomerLabel(CustomerLabel customerLabel, String creator);

    /**
     * 批量新增标签
     *
     * @param customerLabels 标签列表
     * @param creator 创建人
     * @return 成功添加的数量
     */
    int batchAddCustomerLabels(List<CustomerLabel> customerLabels, String creator);

    /**
     * 删除标签（逻辑删除）
     *
     * @param id 标签ID
     * @param editor 操作人
     * @return 是否成功
     */
    boolean deleteCustomerLabel(String id, String editor);

    /**
     * 根据创建人查询标签列表
     *
     * @param creator 创建人
     * @return 标签列表
     */
    List<CustomerLabel> getCustomerLabelsByCreator(String creator);
}
