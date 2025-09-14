package cn.visolink.system.channel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 亮
 * @Description: 16个分子分母变量
 * @date 2025/8/21 18:38
 */
@Data
public class NumeratorAndDenominator implements Serializable {

    /**
     * 新增分母
     */
    private Map<String, List<Map>> customerAddLogList;

    /**
     * 拜访分子（新增转拜访率）
     */
    private Map<String, List<Map>> addVisit;

    /**
     * 三个一分子（新增转三个率）
     */
    private Map<String, List<Map>> addThreeOne;

    /**
     * 来访分子（新增转来访率）
     */
    private Map<String, List<Map>> addDaoFang;

    /**
     * 拜访分母
     */
    private Map<String, List<Map>> allCustomervisit;

    /**
     * 首访分子（拜访转首访率）
     */
    private Map<String, List<Map>> visitShouFang;

    /**
     * 三个一分母
     */
    private Map<String, List<Map>> allCustomerThreeOne;

    /**
     * 首访分子（三个一转首访率）
     */
    private Map<String, List<Map>> threeOneShoufang;

    /**
     * 来访分母
     */
    private Map<String, List<Map>> allCustomerDaoFang;

    /**
     * 成交分子（来访转成交率）
     */
    private Map<String, List<Map>> daoFangChengjiao;

    /**
     * 首访分母
     */
    private Map<String, List<Map>> allCustomershouFang;

    /**
     * 复访分子（首访转复访率）
     */
    private Map<String, List<Map>> shoufangFufang;

    /**
     * 复访分母
     */
    private Map<String, List<Map>> allCustomerfuFang;

    /**
     * 成交分子（复访转成交率）
     */
    private Map<String, List<Map>> fufangChengjiao;

    /**
     * 成交分母
     */
    private Map<String, List<Map>> allCustomerchengjiao;

    /**
     * 复购分子（成交转复购率）
     */
    private Map<String, List<Map>> chengjiaoFugou;
}
