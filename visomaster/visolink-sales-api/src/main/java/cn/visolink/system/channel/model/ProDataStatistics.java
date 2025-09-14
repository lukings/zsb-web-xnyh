package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ProDataStatistics
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/6 15:27
 **/
@ApiModel(value="项目数据统计", description="项目数据统计")
@Data
public class ProDataStatistics {

    @ApiModelProperty(value = "序号")
    private String rownum;
    @ApiModelProperty(value = "项目ID")
    private String projectId;
    private String projectName;
    @ApiModelProperty(value = "分类名称（项目或业务员）")
    private String id;
    private String name;
    @ApiModelProperty(value = "区域名称")
    private String areaName;
    @ApiModelProperty(value = "团队名称")
    private String orgName;
    @ApiModelProperty(value = "账号")
    private String userId;
    private String userName;
    @ApiModelProperty(value = "机会总数")
    private String oppSum;
    @ApiModelProperty(value = "新增客户数")
    private String addSum;
    @ApiModelProperty(value = "成交客户数")
    private String orderSum;
    @ApiModelProperty(value = "未成交客户数")
    private String reportSum;
    @ApiModelProperty(value = "新增机会数")
    private String newOppCount;
    @ApiModelProperty(value = "电话、微信")
    private String callCount;
    @ApiModelProperty(value = "拜访数")
    private String visitCount;
    @ApiModelProperty(value = "首拜数")
    private String firstComeVisitCount;
    @ApiModelProperty(value = "三个一前复拜数")
    private String threeOnesBeforeReComeVisitCount;
    @ApiModelProperty(value = "三个一后复拜数")
    private String threeOnesAfterReComeVisitCount;
    @ApiModelProperty(value = "三个一")
    private String threeOnesCount;
    @ApiModelProperty(value = "实际三个一")
    private String sjThreeOnesCount;
    @ApiModelProperty(value = "计算三个一")
    private String jsThreeOnesCount;
    @ApiModelProperty(value = "首访数")
    private String firstVisitCount;
    @ApiModelProperty(value = "复访数")
    private String reVisitCount;
    @ApiModelProperty(value = "来访数")
    private String followVisitCount;
    @ApiModelProperty(value = "A类客户数")
    private String aCount;
    @ApiModelProperty(value = "B类客户数")
    private String bCount;
    @ApiModelProperty(value = "C类客户数")
    private String cCount;
    @ApiModelProperty(value = "D类客户数")
    private String dCount;

    @ApiModelProperty(value = "客户名称")
    private String customerName;
    @ApiModelProperty(value = "成交周期（天）")
    private String dealCycle;
    @ApiModelProperty(value = "平均成交周期")
    private String avgDealCycle;
    @ApiModelProperty(value = "最长成交周期")
    private String maxDealCycle;
    @ApiModelProperty(value = "最短成交周期")
    private String minDealCycle;
    @ApiModelProperty(value = "复购周期（天）")
    private String reDealCycle;
    @ApiModelProperty(value = "平均复购周期")
    private String avgReDealCycle;
    @ApiModelProperty(value = "最长复购周期")
    private String maxReDealCycle;
    @ApiModelProperty(value = "最短复购周期")
    private String minReDealCycle;
    @ApiModelProperty(value = "搜索类型")
    private String type;
    @ApiModelProperty(value = "客户ID")
    private String opportunityClueId;

    @ApiModelProperty(value = "来访工作量")
    private String followVisitWorkload;

    /**
     * 获取项目数据
     * @param
     * @return
     */
    public String[]  cardTitle1 =  new String[]{
            "序号","区域","项目","报备客户总数","新增客户数","成交客户数","电话/微信",
            "拜访数","三个一","首访数","复访数","A类客户","B类客户","C类客户","D类客户"};


    public Object[] toData1( List<String> fileds) {
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("areaName")){
            met.add(getAreaName());
        }
        if(fileds.contains("name")){
            met.add(getName());
        }
        if(fileds.contains("oppSum")){
            met.add(getOppSum());
        }
        if(fileds.contains("addSum")){
            met.add(getAddSum());
        }
        if(fileds.contains("orderSum")){
            met.add(getOrderSum());
        }
        if(fileds.contains("callCount")){
            met.add(getCallCount());
        }
        if(fileds.contains("visitCount")){
            met.add(getVisitCount());
        }
        if(fileds.contains("firstComeVisitCount")){
            met.add(getFirstComeVisitCount());
        }
        if(fileds.contains("threeOnesBeforeReComeVisitCount")){
            met.add(getThreeOnesBeforeReComeVisitCount());
        }
        if(fileds.contains("threeOnesAfterReComeVisitCount")){
            met.add(getThreeOnesAfterReComeVisitCount());
        }
        if(fileds.contains("threeOnesCount")){
            met.add(getThreeOnesCount());
        }
        if(fileds.contains("sjThreeOnesCount")){
            met.add(getSjThreeOnesCount());
        }
        if(fileds.contains("jsThreeOnesCount")){
            met.add(getJsThreeOnesCount());
        }
        if(fileds.contains("firstVisitCount")){
            met.add(getFirstVisitCount());
        }
        if(fileds.contains("reVisitCount")){
            met.add(getReVisitCount());
        }
        if(fileds.contains("followVisitCount")){
            met.add(getFollowVisitCount());
        }
        if(fileds.contains("acount")){
            met.add(getACount());
        }
        if(fileds.contains("bcount")){
            met.add(getBCount());
        }
        if(fileds.contains("ccount")){
            met.add(getCCount());
        }
        if(fileds.contains("dcount")){
            met.add(getDCount());
        }
        if(fileds.contains("followVisitWorkload")){
            met.add(getFollowVisitWorkload());
        }


        Object[] objects = met.toArray();
        return objects;


//        return new Object[]{
//                getRownum(),getName(),getOppSum(),getAddSum(),getOrderSum(),getCallCount(),
//                getVisitCount(),getThreeOnesCount(),getFirstVisitCount(),getReVisitCount(),getACount(),
//                getBCount(),getCCount(),getDCount()
//        };
    }
    /**
     * 获取项目成员数据
     * @param
     * @return
     */
    public String[]  cardTitle2 =  new String[]{
            "序号","项目成员","账号","报备客户总数","新增客户数","成交客户数","电话/微信",
            "拜访数","三个一","首访数","复访数","A类客户","B类客户","C类客户","D类客户"};

    public Object[] toData2( List<String> fileds) {
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("areaName")){
            met.add(getAreaName());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("orgName")){
            met.add(getOrgName());
        }
        if(fileds.contains("name")){
            met.add(getName());
        }
        if(fileds.contains("userName")){
            met.add(getUserName());
        }
        if(fileds.contains("oppSum")){
            met.add(getOppSum());
        }
        if(fileds.contains("addSum")){
            met.add(getAddSum());
        }
        if(fileds.contains("orderSum")){
            met.add(getOrderSum());
        }
        if(fileds.contains("callCount")){
            met.add(getCallCount());
        }
        if(fileds.contains("visitCount")){
            met.add(getVisitCount());
        }
        if(fileds.contains("firstComeVisitCount")){
            met.add(getFirstComeVisitCount());
        }
        if(fileds.contains("threeOnesBeforeReComeVisitCount")){
            met.add(getThreeOnesBeforeReComeVisitCount());
        }
        if(fileds.contains("threeOnesAfterReComeVisitCount")){
            met.add(getThreeOnesAfterReComeVisitCount());
        }
        if(fileds.contains("threeOnesCount")){
            met.add(getThreeOnesCount());
        }
        if(fileds.contains("sjThreeOnesCount")){
            met.add(getSjThreeOnesCount());
        }
        if(fileds.contains("jsThreeOnesCount")){
            met.add(getJsThreeOnesCount());
        }
        if(fileds.contains("firstVisitCount")){
            met.add(getFirstVisitCount());
        }
        if(fileds.contains("reVisitCount")){
            met.add(getReVisitCount());
        }
        if(fileds.contains("followVisitCount")){
            met.add(getFollowVisitCount());
        }
        if(fileds.contains("acount")){
            met.add(getACount());
        }
        if(fileds.contains("bcount")){
            met.add(getBCount());
        }
        if(fileds.contains("ccount")){
            met.add(getCCount());
        }
        if(fileds.contains("dcount")){
            met.add(getDCount());
        }
        if(fileds.contains("followVisitWorkload")){
            met.add(getFollowVisitWorkload());
        }
        Object[] objects = met.toArray();
        return objects;
//        return new Object[]{
//                getRownum(),getName(),getUserName(),getOppSum(),getAddSum(),getOrderSum(),getCallCount(),
//                getVisitCount(),getThreeOnesCount(),getFirstVisitCount(),getReVisitCount(),getACount(),
//                getBCount(),getCCount(),getDCount()
//        };
    }

    /**
     * 获取区域数据
     * @param
     * @return
     */
    public String[]  cardTitle3 =  new String[]{
            "序号","区域","报备客户总数","新增客户数","成交客户数","电话/微信",
            "拜访数","三个一","首访数","复访数","A类客户","B类客户","C类客户","D类客户"};

    public Object[] toData3(List<String> fileds) {
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("name")){
            met.add(getName());
        }
        if(fileds.contains("oppSum")){
            met.add(getOppSum());
        }
        if(fileds.contains("addSum")){
            met.add(getAddSum());
        }
        if(fileds.contains("orderSum")){
            met.add(getOrderSum());
        }
        if(fileds.contains("callCount")){
            met.add(getCallCount());
        }
        if(fileds.contains("visitCount")){
            met.add(getVisitCount());
        }
        if(fileds.contains("firstComeVisitCount")){
            met.add(getFirstComeVisitCount());
        }
        if(fileds.contains("threeOnesBeforeReComeVisitCount")){
            met.add(getThreeOnesBeforeReComeVisitCount());
        }
        if(fileds.contains("threeOnesAfterReComeVisitCount")){
            met.add(getThreeOnesAfterReComeVisitCount());
        }
        if(fileds.contains("threeOnesCount")){
            met.add(getThreeOnesCount());
        }
        if(fileds.contains("sjThreeOnesCount")){
            met.add(getSjThreeOnesCount());
        }
        if(fileds.contains("jsThreeOnesCount")){
            met.add(getJsThreeOnesCount());
        }
        if(fileds.contains("firstVisitCount")){
            met.add(getFirstVisitCount());
        }
        if(fileds.contains("reVisitCount")){
            met.add(getReVisitCount());
        }
        if(fileds.contains("followVisitCount")){
            met.add(getFollowVisitCount());
        }
        if(fileds.contains("acount")){
            met.add(getACount());
        }
        if(fileds.contains("bcount")){
            met.add(getBCount());
        }
        if(fileds.contains("ccount")){
            met.add(getCCount());
        }
        if(fileds.contains("dcount")){
            met.add(getDCount());
        }
        if(fileds.contains("followVisitWorkload")){
            met.add(getFollowVisitWorkload());
        }
        Object[] objects = met.toArray();
        return objects;
//        return new Object[]{
//                getRownum(),getName(),getOppSum(),getAddSum(),getOrderSum(),getCallCount(),
//                getVisitCount(),getThreeOnesCount(),getFirstVisitCount(),getReVisitCount(),getACount(),
//                getBCount(),getCCount(),getDCount()
//        };
    }

    /**
     * 获取区域成员数据
     * @param
     * @return
     */
    public String[]  cardTitle4 =  new String[]{
            "序号","区域成员","账号","报备客户总数","新增客户数","成交客户数","电话/微信",
            "拜访数","三个一","首访数","复访数","A类客户","B类客户","C类客户","D类客户"};

    public Object[] toData4(List<String> fileds) {
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("areaName")){
            met.add(getAreaName());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("orgName")){
            met.add(getOrgName());
        }
        if(fileds.contains("name")){
            met.add(getName());
        }
        if(fileds.contains("userName")){
            met.add(getUserName());
        }
        if(fileds.contains("oppSum")){
            met.add(getOppSum());
        }
        if(fileds.contains("addSum")){
            met.add(getAddSum());
        }
        if(fileds.contains("orderSum")){
            met.add(getOrderSum());
        }
        if(fileds.contains("callCount")){
            met.add(getCallCount());
        }
        if(fileds.contains("visitCount")){
            met.add(getVisitCount());
        }
        if(fileds.contains("firstComeVisitCount")){
            met.add(getFirstComeVisitCount());
        }
        if(fileds.contains("threeOnesBeforeReComeVisitCount")){
            met.add(getThreeOnesBeforeReComeVisitCount());
        }
        if(fileds.contains("threeOnesAfterReComeVisitCount")){
            met.add(getThreeOnesAfterReComeVisitCount());
        }
        if(fileds.contains("threeOnesCount")){
            met.add(getThreeOnesCount());
        }
        if(fileds.contains("sjThreeOnesCount")){
            met.add(getSjThreeOnesCount());
        }
        if(fileds.contains("jsThreeOnesCount")){
            met.add(getJsThreeOnesCount());
        }
        if(fileds.contains("firstVisitCount")){
            met.add(getFirstVisitCount());
        }
        if(fileds.contains("reVisitCount")){
            met.add(getReVisitCount());
        }
        if(fileds.contains("followVisitCount")){
            met.add(getFollowVisitCount());
        }
        if(fileds.contains("acount")){
            met.add(getACount());
        }
        if(fileds.contains("bcount")){
            met.add(getBCount());
        }
        if(fileds.contains("ccount")){
            met.add(getCCount());
        }
        if(fileds.contains("dcount")){
            met.add(getDCount());
        }
        if(fileds.contains("followVisitWorkload")){
            met.add(getFollowVisitWorkload());
        }
        Object[] objects = met.toArray();
        return objects;
//        return new Object[]{
//                getRownum(),getName(),getUserName(),getOppSum(),getAddSum(),getOrderSum(),getCallCount(),
//                getVisitCount(),getThreeOnesCount(),getFirstVisitCount(),getReVisitCount(),getACount(),
//                getBCount(),getCCount(),getDCount()
//        };
    }

    public Object[] toData5( List<String> fileds) {
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("areaName")){
            met.add(getAreaName());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("customerName")){
            met.add(getCustomerName());
        }
        if(fileds.contains("dealCycle")){
            met.add(getDealCycle());
        }
        if(fileds.contains("visitCount")){
            met.add(getVisitCount());
        }
        if(fileds.contains("reDealCycle")){
            met.add(getReDealCycle());
        }

        Object[] objects = met.toArray();
        return objects;
    }

}
