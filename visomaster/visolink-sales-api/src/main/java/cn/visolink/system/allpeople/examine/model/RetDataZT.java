package cn.visolink.system.allpeople.examine.model;

import lombok.Data;

/**
 * @ClassName RetDataZT
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/8/13 10:44
 **/
@Data
public class RetDataZT {

    private String projId;
    /**
     *业绩归属人ID
     */
    private String pfmcAttrId;
    /**
     *认购数量
     */
    private int orderCnt;
    /**
     *签约数量
     */
    private int cntrtCnt;

    /**
     *认购面积
     */
    private double orderArea;

    /**
     *认购金额
     */
    private double orderAmt;
    /**
     *签约金额
     */
    private double cntrtAmt;

    /**
     *签约面积
     */
    private double cntrtArea;

}
