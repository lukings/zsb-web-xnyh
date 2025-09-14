package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DealStatistics
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/6 16:29
 **/
@ApiModel(value="成交数据统计", description="成交数据统计")
@Data
public class DealStatistics {

    @ApiModelProperty(value = "序号")
    private String rownum;
    @ApiModelProperty(value = "项目ID")
    private String projectId;
    @ApiModelProperty(value = "分类名称（项目或业务员）")
    private String name;
    @ApiModelProperty(value = "成交套数")
    private String dealCount;
    @ApiModelProperty(value = "成交金额")
    private String dealAmount;

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
        if(fileds.contains("dealCount")){
            met.add(getDealCount());
        }
        if(fileds.contains("dealAmount")){
            met.add(getDealAmount());
        }

        Object[] objects = met.toArray();
        return objects;
//        return new Object[]{
//                getRownum(),getName(),getDealCount(),getDealAmount()
//        };
    }

    public String[]  cardTitle1 =  new String[]{
            "序号","项目","成交套数","成交金额"};

    public String[]  cardTitle2 =  new String[]{
            "序号","项目成员","成交套数","成交金额"};

    public String[]  cardTitle3 =  new String[]{
            "序号","区域","成交套数","成交金额"};

}
