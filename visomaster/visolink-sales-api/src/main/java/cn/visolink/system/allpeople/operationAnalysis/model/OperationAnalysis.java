package cn.visolink.system.allpeople.operationAnalysis.model;

import lombok.Data;

/**
 * @ClassName OperationAnalysis
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/4/13 20:23
 **/
@Data
public class OperationAnalysis {

    private int num;
    private String AreaName;//区域
    private String ProjectName;//项目
    private String ProjectId;//项目ID
    private String ProjectNum;//项目编码
    private String CityID;//城市ID
    private String CityName;//城市名称
    private String BookAllVisitsCount;//楼书累计访问人数
    private String BookSectionVisitsCount;//楼书区间访问人数
    private String RegCstCount;//注册经纪人累计数量
    private String SectionRegCstCount;//区间内注册经纪人数量
    private String PRegCstCount;//注册经纪人累计数量(普通经纪人)
    private String YRegCstCount;//注册经纪人累计数量(业主)
    private String GRegCstCount;//注册经纪人累计数量(公司员工)
    private String HRegCstCount;//注册经纪人累计数量(合作方)
    private String SectionPRegCstCount;//区间内注册经纪人数量(普通经纪人)
    private String SectionYRegCstCount;//区间内注册经纪人数量(业主)
    private String SectionGRegCstCount;//区间内注册经纪人数量(公司员工)
    private String SectionHRegCstCount;//区间内注册经纪人数量(合作方)

    private String RecCount;//经纪人推荐量
    private String SectionRecCount;//区间内经纪人推荐量
    private String VisitCount;//经纪人推荐到访量
    private String SectionVisitCount;//区间内经纪人推荐到访量

    private String OrderCount;//经纪人推荐认购套数
    private String OrderAMT;//经纪人推荐认购金额
    private String SectionOrderCount;//区间内经纪人推荐认购套数
    private String ContractCount;//经纪人推荐签约套数
    private String ContractAMT;//经纪人推荐签约金额
    private String SectionContractCount;//区间内经纪人推荐签约套数
    /**
     * 获取分析数据
     * @param
     * @return
     */
    public Object[] toExproData(){
        return new Object[]{
                getNum(),getAreaName(),getProjectName(),getProjectNum(),getCityName(),getBookAllVisitsCount(),
                getRegCstCount(),getPRegCstCount(),getYRegCstCount(),getGRegCstCount(),getHRegCstCount(),
                getSectionRegCstCount(),getSectionPRegCstCount(),getSectionYRegCstCount(),getSectionGRegCstCount(),
                getSectionHRegCstCount(),getRecCount(),getVisitCount(),getOrderCount(),getOrderAMT(),getContractCount(),
                getContractAMT()
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "序号","区域","项目","项目编码","所属城市",
            "微楼书累计访问人数","累计注册经纪人数量","普通经纪人数量",
            "业主数量","公司员工数量","合作方数量","区间内注册经纪人数量"
            ,"区间内普通经纪人数量","区间内业主数量","区间内公司员工数量",
            "区间内合作方数量","经纪人推荐量","经纪人推荐到访量","经纪人推荐认购套数",
            "经纪人推荐认购金额","经纪人推荐签约套数","经纪人推荐签约金额"};
}
