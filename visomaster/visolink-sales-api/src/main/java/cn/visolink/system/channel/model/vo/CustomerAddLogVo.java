package cn.visolink.system.channel.model.vo;

import cn.visolink.system.channel.model.CustomerFodLog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客户新增日志表
 * @TableName b_customer_add_log
 */
@Data
public class CustomerAddLogVo implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 区域ID
     */
    private String areaId;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 客户ID
     */
    private String opportunityClueId;

    /**
     * 线索客户ID
     */
    private String projectClueId;

    /**
     * 公司名称
     */
    private String customerName;

    /**
     * 联系人手机号
     */
    private String customerMobile;

    /**
     * 案场归属人ID
     */
    private String salesAttributionId;

    /**
     * 案场归属人名称
     */
    private String salesAttributionName;

    /**
     * 案场归属人团队ID
     */
    private String salesAttributionTeamId;

    /**
     * 案场归属人团队名称
     */
    private String salesAttributionTeamName;

    /**
     * 是否完成三个一
     */
    private String isThreeOnes;

    /**
     * 是否三个一时间
     */
    private String isThreeOnesDate;

    /**
     * 新增类型（1 手动录入 2公客池捞取 3转介客户 4万企通推荐客户 5客户重分配）
     */
    private String addType;

    /**
     * 是否新增(0 否 1 是)
     */
    private String isAdd;

    /**
     * 报备时间
     */
    private String reportCreateTime;

    /**
     * 是否有效（1:是 0：否）
     */
    private String isEffective;

    /**
     * 创建时间
     */
    private String createTime;

//    /**
//     * 客户地址
//     */
//    private String customerAddress;
//
//    /**
//     * 客户来源
//     */
//    private String sourceMode;
//
//    /**
//     * 客户一级行业
//     */
//    private String belongIndustrise;
//
//    /**
//     * 客户二级行业
//     */
//    private String belongIndustriseTwo;
//
//    /**
//     * 客户三级行业
//     */
//    private String belongIndustriseThree;
//
//    /**
//     * 客户四级行业
//     */
//    private String belongIndustriseFour;

    /**
     * 客户跟进交易集合
     */
    private List<CustomerFodLogVo> customerFodLogList;

    private static final long serialVersionUID = 1L;
}