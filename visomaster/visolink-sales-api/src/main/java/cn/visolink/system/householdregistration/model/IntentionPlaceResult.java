package cn.visolink.system.householdregistration.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName IntentionPlaceResult
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/8/3 10:01
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IntentionPlaceResult", description="装户结果记录表")
public class IntentionPlaceResult extends Page implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    private int rownum;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "集团区域")
    private String areaname;

    @ApiModelProperty(value = "装户项目ID")
    private String projectid;

    @ApiModelProperty(value = "项目名称")
    private String projectname;

    @ApiModelProperty(value = "所属楼盘ID")
    private String buildbookId;

    @ApiModelProperty(value = "所属楼盘")
    private String buildbookName;

    @ApiModelProperty(value = "分期项目ID")
    private String projectidFq;

    @ApiModelProperty(value = "分期项目名称")
    private String projectnameFq;

    @ApiModelProperty(value = "楼栋ID")
    private String buildguid;

    @ApiModelProperty(value = "楼栋名称")
    private String buildname;

    @ApiModelProperty(value = "单元")
    private String unitname;

    @ApiModelProperty(value = "房间ID")
    private String roomguid;

    @ApiModelProperty(value = "房间编号")
    private String roomno;

    @ApiModelProperty(value = "房间名称")
    private String roomname;

    @ApiModelProperty(value = "销售状态")
    private String saleStatus;

    @ApiModelProperty(value = "房间类型")
    private String roomType;

    @ApiModelProperty(value = "户型")
    private String houseType;

    @ApiModelProperty(value = "户型ID")
    private String houseTypeId;

    @ApiModelProperty(value = "排卡名称")
    private String cardName;

    @ApiModelProperty(value = "开盘批次")
    private String batchNoName;

    @ApiModelProperty(value = "排卡分组")
    private String cardGroupingName;

    @ApiModelProperty(value = "建筑面积")
    private String floorArea;

    @ApiModelProperty(value = "套内面积")
    private String insideArea;

    @ApiModelProperty(value = "建筑单价")
    private String floorPrice;

    @ApiModelProperty(value = "套内单价")
    private String insidePrice;

    @ApiModelProperty(value = "总价")
    private String totalPrice;

    @ApiModelProperty(value = "机会ID")
    private String opportunityClueId;

    @ApiModelProperty(value = "线索ID")
    private String projectClueId;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "客户电话(隐号)")
    private String customerMobile;

    @ApiModelProperty(value = "客户电话(全号)")
    private String customerMobileAll;

    @ApiModelProperty(value = "置业顾问ID")
    private String salesId;

    @ApiModelProperty(value = "置业顾问名称")
    private String salesName;

    @ApiModelProperty(value = "意向级别")
    private String intentionLevel;

    @ApiModelProperty(value = "装户时间")
    private String createTime;

    @ApiModelProperty(value = "操作人ID")
    private String creator;

    @ApiModelProperty(value = "是否全号导出（1：全号 0：隐号）")
    private String isAll = "0";

    @ApiModelProperty(value = "项目集合")
    private List<String> projectList;
    @ApiModelProperty(value = "活动类型（1：旭客家 2：职业顾问）")
    private String loadingType;

    @ApiModelProperty(value = "装户版本")
    private String edition;

    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toActivityResultData(String isAll){
        String mobile = "";
        if ("1".equals(isAll)){
            mobile = getCustomerMobileAll();
        }else{
            mobile = getCustomerMobile();
        }
        return new Object[]{
                getRownum(),getAreaname(),getProjectname(),getCustomerName(),mobile,getSalesName(),
                getActivityName(),getIntentionLevel(),getBuildname(),getUnitname(),getRoomname(),getFloorPrice(),
                getInsidePrice(),getHouseType(),getRoomType(),getCardName(),getBatchNoName(),getCardGroupingName(),
                getFloorArea(),getInsideArea(),getCreateTime()
        };
    }

    public String[]  activityResultTitle =  new String[]{
            "序号","集团事业部","项目","客户姓名","客户电话","置业顾问",
            "活动名称","意向级别","楼栋","单元","房间","建筑单价",
            "套内单价","户型","房间类型","卡号","开盘批次","排卡分组",
            "建筑面积","套内面积","装户时间"};
}
