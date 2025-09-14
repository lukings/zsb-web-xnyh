package cn.visolink.system.householdregistration.model.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import lombok.Data;
import java.io.Serializable;
/**
 * <p>
 * IntentionPlaceVO对象
 * </p>
 *
 * @author autoJob
 * @since 2020-07-29
 */
@Data
@ApiModel(value="IntentionPlace对象", description="装户活动表")
public class IntentionPlaceVO implements Serializable {

    private static final long serialVersionUID = 1L;

        private String id;

        private String rownum;

        @ApiModelProperty(value = "事业部")
        private String areaName;

        @ApiModelProperty(value = "城市id")
        private String cityId;

        @ApiModelProperty(value = "城市名称")
        private String cityName;

        @ApiModelProperty(value = "活动名称")
        private String activityName;

        @ApiModelProperty(value = "装户类型")
        private String loadingType;

        @ApiModelProperty(value = "装户项目ID")
        private String projectid;

        @ApiModelProperty(value = "项目名称")
        private String projectname;

        @ApiModelProperty(value = "所属楼盘ID")
        private String buildbookId;

        @ApiModelProperty(value = "所属楼盘")
        private String buildbookName;

        @ApiModelProperty(value = "分享文案")
        private String activitySharecont;

        @ApiModelProperty(value = "发布时间")
        private String releaseTime;

        @ApiModelProperty(value = "禁用时间")
        private String disabletime;

        @ApiModelProperty(value = "活动开始时间")
        private String activityBegintime;

        @ApiModelProperty(value = "活动结束时间")
        private String activityEndtime;

        @ApiModelProperty(value = "活动规则说明")
        private String activityNote;

        @ApiModelProperty(value = "0报备，1到访，3小卡，4大卡")
        private String customerType;

        @ApiModelProperty(value = "排卡类型")
        private String cardType;

        @ApiModelProperty(value = "装户顺序")
        private String placeNo;

        @ApiModelProperty(value = "意向房源个数")
        private String intentionCount;

        @ApiModelProperty(value = "1不展示，2预估价（楼栋均价*房间建筑面积），3真实价格")
        private String showpriceType;

        @ApiModelProperty(value = "0禁用，1启用")
        private String status;

        @ApiModelProperty(value = "活动状态:1草稿，2已发布")
        private String actStatus;

        @ApiModelProperty(value = "1已发布,0未发布")
        private String ispublish2cst;

        @ApiModelProperty(value = "1已发布,0未发布")
        private String ispublish2gw;

        @ApiModelProperty(value = "是否删除:1已删除，0未删除")
        private String isdel;

        @ApiModelProperty(value = "创建时间")
        private String createtime;

        @ApiModelProperty(value = "创建人")
        private String creator;

        @ApiModelProperty(value = "修改时间")
        private String edittime;

        @ApiModelProperty(value = "修改人")
        private String editor;

        @ApiModelProperty(value = "已装户数量")
        private String placeCount;

        @ApiModelProperty(value = "待装户数量")
        private String needPlaceCount;

        @ApiModelProperty(value = "是否调整")
        private String isEdit;

    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toActivityData(){
        return new Object[]{
                getRownum(),getActivityName(),getLoadingType(),getAreaName(),getProjectname(),
                getActivityBegintime(),getActivityEndtime(),
                getCreator(),getCreatetime(),getReleaseTime(),getDisabletime(),
                getNeedPlaceCount(),getPlaceCount(),getActStatus(),
                getIspublish2cst(),getIspublish2gw()
        };
    }

    public String[]  activityTitle =  new String[]{
            "序号","活动名称","装户类型","区域","项目",
            "装户开始时间","装户截止时间","创建人","创建时间",
            "发布时间","禁用时间","待装户","已装户","状态","发布旭客家","发布置业顾问"};

}