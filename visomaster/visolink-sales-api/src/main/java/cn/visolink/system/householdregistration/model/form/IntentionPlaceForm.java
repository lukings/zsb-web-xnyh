package cn.visolink.system.householdregistration.model.form;

import cn.visolink.system.householdregistration.model.IntentionPlaceBuild;
import cn.visolink.system.householdregistration.model.IntentionPlaceMaterial;
import cn.visolink.system.householdregistration.model.ProBatchVO;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Data;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * IntentionPlaceForm对象
 * </p>
 *
 * @author autoJob
 * @since 2020-07-29
 */
@Data
@ApiModel(value = "IntentionPlace对象", description = "装户活动表")
public class IntentionPlaceForm extends Page {

    private static final long serialVersionUID = 1L;


    private String id;

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

    @ApiModelProperty(value = "开盘批次")
    private List<ProBatchVO> batchList;

    @ApiModelProperty(value = "大卡选择批次分组")
    private List<List<String>> batchEndList;

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

    @ApiModelProperty(value = "展示活动时间配置: 0：不展示 1：展示开始时间 2：展示起始时间")
    private String isTimeShow;

    @ApiModelProperty(value = "是否展示活动范围:1是，0否")
    private String isRangeShow;

    @ApiModelProperty(value = "创建时间")
    private String createtime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改时间")
    private String edittime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "原有装户楼栋")
    private List<Map> builds;

    @ApiModelProperty(value = "装户楼栋房间")
    private List<IntentionPlaceBuild> buildList;

    @ApiModelProperty(value = "装户楼栋坐标")
    private List<Map> buildSites;

    @ApiModelProperty(value = "装户活动素材集合")
    private List<IntentionPlaceMaterial> materialList;

    @ApiModelProperty(value = "项目集合")
    private List<String> projectList;

    @ApiModelProperty(value = "分期项目集合（选择排小卡时）")
    private List<String> fprojectList;

    @ApiModelProperty(value = "时间查询类型（1：发布时间 2：开启时间 3：结束时间）")
    private String reportTime;

    @ApiModelProperty(value = "查询开始时间")
    private String date1;

    @ApiModelProperty(value = "查询结束时间")
    private String date2;

    @ApiModelProperty(value = "已装户数量")
    private String placeCount;

    @ApiModelProperty(value = "是否调整")
    private String isEdit;

}