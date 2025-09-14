package cn.visolink.system.householdregistration.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 装户活动表
 * </p>
 *
 * @author autoJob
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("a_intention_place")
@ApiModel(value="IntentionPlace对象", description="装户活动表")
public class IntentionPlace implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "城市id")
    private String cityId;

    @ApiModelProperty(value = "城市名称")
    private String cityName;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "活动类型")
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

    @ApiModelProperty(value = "活动开始时间")
    private String activityBegintime;

    @ApiModelProperty(value = "活动结束时间")
    private String activityEndtime;

    @ApiModelProperty(value = "活动规则说明")
    private String activityNote;

    @ApiModelProperty(value = "1到访及以后，2排卡")
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
    @TableLogic
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


}
