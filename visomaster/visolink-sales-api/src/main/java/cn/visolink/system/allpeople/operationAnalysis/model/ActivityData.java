package cn.visolink.system.allpeople.operationAnalysis.model;

import lombok.Data;

/**
 * @ClassName ActivityData
 * @Author wanggang
 * @Description //活动数据（中台）
 * @Date 2020/9/22 14:56
 **/
@Data
public class ActivityData {

    /**
     *活动ID
     */
    private String actyId;
    /**
     *活动签约面积
     */
    private double cntrtArea;

    /**
     *活动签约金额
     */
    private double cntrtAmt;
    /**
     *活动认购面积
     */
    private double orderArea;

    /**
     *活动认购金额
     */
    private double orderAmt;

    /**
     *活动认购数量
     */
    private int orderCnt;
    /**
     *活动签约数量
     */
    private int cntrtCnt;
}
