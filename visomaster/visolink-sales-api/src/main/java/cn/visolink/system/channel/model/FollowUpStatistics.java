package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FollowUpStatistics
 * @Author wanggang
 * @Description //跟进数据统计
 * @Date 2022/4/6 15:47
 **/
@ApiModel(value="跟进数据统计", description="跟进数据统计")
@Data
public class FollowUpStatistics {

    @ApiModelProperty(value = "序号")
    private String rownum;
    @ApiModelProperty(value = "项目ID")
    private String projectId;
    @ApiModelProperty(value = "分类名称（项目或业务员）")
    private String name;
    @ApiModelProperty(value = "客户数")
    private String oppCount;
    @ApiModelProperty(value = "跟进客户数")
    private String followUpCount;
    @ApiModelProperty(value = "跟进率")
    private String followUpRate;
    @ApiModelProperty(value = "来访客户数")
    private String visitCount;
    @ApiModelProperty(value = "来访率")
    private String visitRate;
    @ApiModelProperty(value = "首访客户数")
    private String firstVisitCount;
    @ApiModelProperty(value = "首访率")
    private String firstVisitRate;

    /**
     * 获取跟进数据
     * @param
     * @return
     */
    public Object[] toData1() {
        return new Object[]{
                getRownum(),getName(),getOppCount(),getFollowUpCount(),
                getFollowUpRate(),getVisitCount(),getVisitRate(),getFirstVisitCount(),getFirstVisitRate()
        };
    }

    public String[]  cardTitle1 =  new String[]{
            "序号","项目","机会客户数","跟进客户数",
            "跟进率","来访客户数","来访率","首访客户数","首访率"};
}
