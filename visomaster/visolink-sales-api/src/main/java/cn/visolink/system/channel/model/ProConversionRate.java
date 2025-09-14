package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ProConversionRate
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/9/1 16:11
 **/
@ApiModel(value="项目转化率统计", description="项目转化率统计")
@Data
public class ProConversionRate implements Serializable {

    @ApiModelProperty(value = "序号")
    private String rownum;
    @ApiModelProperty(value = "分类名称（项目或区域）")
    private String name;
    @ApiModelProperty(value = "机会总数")
    private String oppSum;
    @ApiModelProperty(value = "成交客户数")
    private String orderSum;
    @ApiModelProperty(value = "跟进数")
    private String followCount;
    @ApiModelProperty(value = "到访数")
    private String visitCount;
    @ApiModelProperty(value = "三个一")
    private String threeOnesCount;
    @ApiModelProperty(value = "到访率")
    private String visitRate;
    @ApiModelProperty(value = "成交率")
    private String orderRate;
    @ApiModelProperty(value = "三个一转化率")
    private String threeOnesRate;
    @ApiModelProperty(value = "跟进率")
    private String followRate;

    @ApiModelProperty(value = "新增转拜访率")
    private String addToComeVisitRate;
    @ApiModelProperty(value = "新增转三个一率")
    private String addToThreeOnesRate;
    @ApiModelProperty(value = "新增转来访率")
    private String addToFollowVisitRate;
    @ApiModelProperty(value = "拜访转来访率")
    private String comeVisitToFollowVisitRate;
    @ApiModelProperty(value = "三个一转来访率")
    private String threeOnesToFollowVisitRate;
    @ApiModelProperty(value = "首访转复访率")
    private String firstFollowVisitToReFollowVisitRate;
    @ApiModelProperty(value = "复访转成交率")
    private String reFollowVisitToTradeRate;
    @ApiModelProperty(value = "来访转成交率")
    private String followVisitToTradeRate;
    @ApiModelProperty(value = "累计复购率")
    private String reTradeRate;

    /**
     * 获取项目数据
     * @param
     * @return
     */
    public Object[] toData1(List<String> fileds) {
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
        if(fileds.contains("followCount")){
            met.add(getFollowCount());
        }
        if(fileds.contains("followRate")){
            met.add(getFollowRate());
        }
        if(fileds.contains("visitRate")){
            met.add(getVisitRate());
        }
        if(fileds.contains("threeOnesRate")){
            met.add(getThreeOnesRate());
        }
        if(fileds.contains("orderRate")){
            met.add(getOrderRate());
        }
        Object[] objects = met.toArray();
        return objects;
//        return new Object[]{
//                getRownum(),getName(),getOppSum(),getFollowCount(),
//                getFollowRate(),getVisitRate(),getThreeOnesRate(),
//                getOrderRate()
//        };
    }
    public Object[] toData2(List<String> fileds) {
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("name")){
            met.add(getName());
        }
        if(fileds.contains("addToComeVisitRate")){
            met.add(getAddToComeVisitRate());
        }
        if(fileds.contains("addToThreeOnesRate")){
            met.add(getAddToThreeOnesRate());
        }
        if(fileds.contains("addToFollowVisitRate")){
            met.add(getAddToFollowVisitRate());
        }
        if(fileds.contains("comeVisitToFollowVisitRate")){
            met.add(getComeVisitToFollowVisitRate());
        }
        if(fileds.contains("threeOnesToFollowVisitRate")){
            met.add(getThreeOnesToFollowVisitRate());
        }
        if(fileds.contains("firstFollowVisitToReFollowVisitRate")){
            met.add(getFirstFollowVisitToReFollowVisitRate());
        }
        if(fileds.contains("reFollowVisitToTradeRate")){
            met.add(getReFollowVisitToTradeRate());
        }
        if(fileds.contains("followVisitToTradeRate")){
            met.add(getFollowVisitToTradeRate());
        }
        if(fileds.contains("reTradeRate")){
            met.add(getReTradeRate());
        }
        Object[] objects = met.toArray();
        return objects;
//        return new Object[]{
//                getRownum(),getName(),getOppSum(),getFollowCount(),
//                getFollowRate(),getVisitRate(),getThreeOnesRate(),
//                getOrderRate()
//        };
    }

    public String[]  cardTitle1 =  new String[]{
            "序号","项目","报备客户总数","跟进数",
            "跟进率","到访率","三个一转化率","成交率"};

    public String[]  cardTitle3 =  new String[]{
            "序号","区域","报备客户总数","跟进数",
            "跟进率","到访率","三个一转化率","成交率"};

}
