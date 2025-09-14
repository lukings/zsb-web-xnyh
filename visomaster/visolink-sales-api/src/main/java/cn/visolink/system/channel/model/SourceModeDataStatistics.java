package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luqianqian
 * @Description: 客户来源数据统计
 * @date 2024/4/19 16:39
 */
@ApiModel(value="客户来源数据统计", description="客户来源数据统计")
@Data
public class SourceModeDataStatistics {
    @ApiModelProperty(value = "序号")
    private String rownum;
    @ApiModelProperty(value = "项目ID")
    private String projectId;
    private String projectName;
    @ApiModelProperty(value = "分类名称（项目或业务员）")
    private String name;
    @ApiModelProperty(value ="区域id")
    private String comguid;
    private String areaName;
    @ApiModelProperty(value = "账号")
    private String userId;
    private String userName;
    @ApiModelProperty(value = "客户来源")
    private String customerType;
    @ApiModelProperty(value = "总计")
    private String allSum;
    @ApiModelProperty(value = "以商招商客户数")
    private String ibbSum;
    @ApiModelProperty(value = "自然来访客户数")
    private String nvSum;
    @ApiModelProperty(value = "58同城客户数")
    private String fecSum;
    @ApiModelProperty(value = "户外广告客户数")
    private String omaSum;
    @ApiModelProperty(value = "商协会引荐客户数")
    private String barSum;
    @ApiModelProperty(value = "政府推荐客户数")
    private String grSum;
    @ApiModelProperty(value = "企业陌拜（派单）客户数")
    private String cdSum;
    @ApiModelProperty(value = "Call客客户数")
    private String ckSum;
    @ApiModelProperty(value = "横幅海报客户数")
    private String bpSum;
    @ApiModelProperty(value = "短信投放客户数")
    private String sdSum;
    @ApiModelProperty(value = "付费微信朋友圈广告客户数")
    private String pwfSum;
    @ApiModelProperty(value = "搜索引擎推广客户数")
    private String sepSum;
    @ApiModelProperty(value = "万企通客户数")
    private String wqtSum;
    @ApiModelProperty(value = "其他新媒体渠道客户数")
    private String otcSum;
    @ApiModelProperty(value = "彩信投放客户数")
    private String mmdSum;
    @ApiModelProperty(value = "付费抖音/今日头条广告客户数")
    private String thaSum;
    @ApiModelProperty(value = "集团分配客户线索客户数")
    private String gacSum;
    @ApiModelProperty(value = "个人微信视频号客户数")
    private String gwvSum;
    @ApiModelProperty(value = "项目微信视频号客户数")
    private String pwvSum;
    @ApiModelProperty(value = "项目抖音号客户数")
    private String pttSum;
    @ApiModelProperty(value = "个人抖音号客户数")
    private String gttSum;
    @ApiModelProperty(value = "展会拓客客户数")
    private String etSum;
//    @ApiModelProperty(value ="报备客户")
//    private SourceModeDataStatistics reportChildren;
//    @ApiModelProperty(value ="新增客户")
//    private SourceModeDataStatistics addChildren;
//    @ApiModelProperty(value ="来访客户")
//    private SourceModeDataStatistics visitChildren;
//    @ApiModelProperty(value ="成交客户")
//    private SourceModeDataStatistics signChildren;
    @ApiModelProperty(value ="子类")
    private List<SourceModeDataStatistics> children = new ArrayList<>();
    @ApiModelProperty(value ="唯一值")
    private String ionRowKeyId;
    @ApiModelProperty(value ="排序值")
    private int orgLevel;
    @ApiModelProperty(value ="排序值")
    private int cusTypeLevel;

    public Object[] toData1( List<String> fileds) {
        List met = new ArrayList();
        if(fileds.contains("NAMET")){
            met.add(getName());
        }
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("name")){
            met.add(getAreaName());
        }
        if(fileds.contains("customerType")){
            met.add(getCustomerType());
        }
        if(fileds.contains("allSum")){
            met.add(getAllSum());
        }
        if(fileds.contains("ibbSum")){
            met.add(getIbbSum());
        }
        if(fileds.contains("nvSum")){
            met.add(getNvSum());
        }
        if(fileds.contains("fecSum")){
            met.add(getFecSum());
        }
        if(fileds.contains("omaSum")){
            met.add(getOmaSum());
        }
        if(fileds.contains("barSum")){
            met.add(getBarSum());
        }
        if(fileds.contains("grSum")){
            met.add(getGrSum());
        }
        if(fileds.contains("cdSum")){
            met.add(getCdSum());
        }
        if(fileds.contains("ckSum")){
            met.add(getCkSum());
        }
        if(fileds.contains("bpSum")){
            met.add(getBpSum());
        }
        if(fileds.contains("sdSum")){
            met.add(getSdSum());
        }
        if(fileds.contains("pwfSum")){
            met.add(getPwfSum());
        }
        if(fileds.contains("sepSum")){
            met.add(getSepSum());
        }
        if(fileds.contains("wqtSum")){
            met.add(getWqtSum());
        }
        if(fileds.contains("otcSum")){
            met.add(getOtcSum());
        }
        if(fileds.contains("mmdSum")){
            met.add(getMmdSum());
        }
        if(fileds.contains("thaSum")){
            met.add(getThaSum());
        }
        if(fileds.contains("gacSum")){
            met.add(getGacSum());
        }
        if(fileds.contains("gwvSum")){
            met.add(getGwvSum());
        }
        if(fileds.contains("pwvSum")){
            met.add(getPwvSum());
        }
        if(fileds.contains("pttSum")){
            met.add(getPttSum());
        }
        if(fileds.contains("gttSum")){
            met.add(getGttSum());
        }
        if(fileds.contains("etSum")){
            met.add(getEtSum());
        }

        Object[] objects = met.toArray();
        return objects;
    }
}
