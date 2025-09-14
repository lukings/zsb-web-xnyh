package cn.visolink.message.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 亮
 * @Description: 超时未消费项目数据表
 * @date 2025/8/27 15:07
 */
@Data
public class OverdueUnconsumedProjectRecord {
    /**
     * 主键ID（自增）
     */
    private Long id;

    /**
     * 账户ID
     */
    private Integer accountId;

    /**
     * 公司ID
     */
    private Integer companyId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 余额
     */
    private BigDecimal money;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否禁用（1是，0否）
     */
    private Integer isDisable;

    /**
     * 是否删除（1是，0否）
     */
    private Integer isDelete;

    /**
     * 添加用户ID
     */
    private Integer createUid;

    /**
     * 修改用户ID
     */
    private Integer updateUid;

    /**
     * 删除用户ID
     */
    private Integer deleteUid;

    /**
     * 添加时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 删除时间
     */
    private String deleteTime;

    /**
     * 未外呼天数
     */
    private Integer notCallDay;

    /**
     * 超时未外呼天数
     */
    private Integer overNotCallDay;

    /**
     * 是否已读（1是，0否）
     */
    private Integer isRead;
}
