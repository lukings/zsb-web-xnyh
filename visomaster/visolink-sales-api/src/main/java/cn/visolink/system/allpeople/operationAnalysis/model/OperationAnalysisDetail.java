package cn.visolink.system.allpeople.operationAnalysis.model;

import lombok.Data;

/**
 * @ClassName OperationAnalysisDetail
 * @Author wanggang
 * @Description //TODO 推荐明细
 * @Date 2020/4/13 20:23
 **/
@Data
public class OperationAnalysisDetail {

    private int num;
    private String AreaName;//区域
    private String ProjectName;//项目
    private String ProjectId;//项目ID
    private String ProjectNum;//项目编码
    private String CityID;//城市ID
    private String CityName;//城市名称

    private String recCount;//经纪人推荐量
    private String ptRecCount;//普通经纪人推荐量
    private String yzRecCount;//业主推荐量
    private String ygRecCount;//员工推荐量
    private String hzfRecCount;//合作方推荐量

    private String visitCount;//经纪人推荐到访量
    private String ptVisitCount;//普通经纪人推荐到访量
    private String yzVisitCount;//业主推荐到访量
    private String ygVisitCount;//员工推荐到访量
    private String hzfVisitCount;//合作方推荐到访量

    private String orderCount;//经纪人推荐认购套数
    private String ptOrderCount;//普通经纪人推荐认购套数
    private String yzOrderCount;//业主推荐认购套数
    private String ygOrderCount;//员工推荐认购套数
    private String hzfOrderCount;//合作方推荐认购套数
    private String orderAMT;//经纪人推荐认购金额

    private String contractCount;//经纪人推荐签约套数
    private String ptContractCount;//普通经纪人推荐签约套数
    private String yzContractCount;//业主推荐签约套数
    private String ygContractCount;//员工推荐签约套数
    private String hzfContractCount;//合作方推荐签约套数
    private String contractAMT;//经纪人推荐签约金额

    private String dynamicAllCnt;//楼盘动态累计量
    private String dynamicReleaseCnt;//楼盘动态发布量
    private String dynamicReleaseTime;//楼盘动态发布时长
    /**
     * 获取分析数据
     * @param
     * @return
     */
    public Object[] toExproData(){
        return new Object[]{
                getNum(),getAreaName(),getProjectName(),getProjectNum(),getCityName(),
                getRecCount(),getPtRecCount(),getYzRecCount(),getYgRecCount(),getHzfRecCount(),
                getVisitCount(),getPtVisitCount(),getYzVisitCount(),getYgVisitCount(),getHzfVisitCount(),
                getOrderCount(),getPtOrderCount(),getYzOrderCount(),getYgOrderCount(),getHzfOrderCount(),getOrderAMT(),
                getContractCount(),getPtContractCount(),getYzContractCount(),getYgContractCount(),getHzfContractCount(),getContractAMT()

        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "序号","区域","项目","项目编码","所属城市",
            "经纪人累计推荐量","普通经纪人推荐量","业主推荐量","员工推荐量","合作方推荐量",
            "经纪人累计推荐到访量","普通经纪人推荐到访量","业主推荐到访量","员工推荐到访量","合作方推荐到访量"
            ,"经纪人累计推荐认购套数","普通经纪人推荐认购套数","业主推荐认购套数","员工推荐认购套数","合作方推荐认购套数","经纪人累计推荐认购金额"
            ,"经纪人累计推荐签约套数","普通经纪人推荐签约套数","业主推荐签约套数","员工推荐签约套数","合作方推荐签约套数","经纪人累计推荐签约金额"};
}
