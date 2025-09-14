package cn.visolink.system.allpeople.operationAnalysis.model;

import lombok.Data;

/**
 * @ClassName RetData
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/5/11 11:46
 **/
@Data
public class RetData {

    private String projId;
    /**
     *普通经纪人推荐数量
     */
    private int agentRptCnt;
    /**
     * 业主推荐数量
     */
    private int ownerRptCnt;
    /**
     *员工推荐数量
     */
    private int employeeRptCnt;
    /**
     *合作方推荐数量
     */
    private int parterRptCnt;
    /**
     *普通经纪人到访量
     */
    private int agentVisitCnt;

    /**
     *业主到访数量
     */
    private int ownerVisitCnt;
    /**
     *员工到访数量
     */
    private int employeeVisitCnt;
    /**
     *合作方到访数量
     */
    private int parterVisitCnt;

    /**
     *合作方认购量
     */
    private int parterOrderCnt;


    /**
     *普通经纪人认购量
     */
    private int agentOrderCnt;
    /**
     *员工认购量
     */
    private int employeeOrderCnt;

    /**
     *业主认购量
     */
    private int ownerOrderCnt;

    /**
     *普通经纪人签约数量
     */
    private int agentCntrtCnt;

    /**
     * 业主签约数量
     */
    private int ownerCntrtCnt;
    /**
     *合作方签约数量
     */
    private int parterCntrtCnt;
    /**
     *员工签约数量
     */
    private int employeeCntrtCnt;

    /**
     *员工签约金额
     */
    private double employeeCntrtAmt;

    /**
     *普通经纪人认购金额
     */
    private double parterOrderAmt;
    /**
     *业主签约金额
     */
    private double ownerCntrtAmt;

    /**
     *员工认购金额
     */
    private double employeeOrderAmt;

    /**
     *普通经纪人认购金额
     */
    private double agentOrderAmt;
    /**
     *业主认购金额
     */
    private double ownerOrderAmt;

    /**
     *普通经纪人签约金额
     */
    private double agentCntrtAmt;

    /**
     *合作方签约金额
     */
    private double parterCntrtAmt;

    /**
     *活动认购数量
     */
    private int actvOrderCnt;
    /**
     *活动签约数量
     */
    private int actvCntrtCnt;
    /**
     *活动认购金额
     */
    private double actvOrderAmt;
    /**
     *活动签约金额
     */
    private double actvCntrtAmt;
    /**
     *正常签约数量
     */
    private int normalCntrtCnt;
    /**
     *正常签约金额
     */
    private double normalCntrtAmt;
    /**
     *正常认购数量
     */
    private int normalOrderCnt;
    /**
     *正常认购金额
     */
    private double normalOrderAmt;

    /**
     *智能话机签约金额
     */
    private double telCntrtAmt;
    /**
     *智慧话机认购数量
     */
    private int telOrderCnt;
    /**
     *智能话机认购金额
     */
    private double telOrderAmt;
    /**
     *智能话机签约数量
     */
    private int telCntrtCnt;
    /**
     *智慧名片认购数量
     */
    private int cardOrderCnt;
    /**
     *智慧名片签约金额
     */
    private double cardCntrtAmt;
    /**
     *智慧名片签约数量
     */
    private int cardCntrtCnt;
    /**
     *智慧名片认购金额
     */
    private double cardOrderAmt;



}
