package cn.visolink.system.householdregistration.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName IntentionPlaceBuild
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/7/29 11:16
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("a_intention_place_build")
@ApiModel(value="装户楼栋", description="装户楼栋表")
public class IntentionPlaceBuild implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "装户活动id")
    private String activityId;

    @ApiModelProperty(value = "项目id")
    private String projectid;

    @ApiModelProperty(value = "项目名称")
    private String projectname;

    @ApiModelProperty(value = "分期id")
    private String projectidFq;

    @ApiModelProperty(value = "分期名称")
    private String projectnameFq;

    @ApiModelProperty(value = "明源楼栋ID")
    private String buildguid;

    @ApiModelProperty(value = "楼栋名称")
    private String buildname;

    @ApiModelProperty(value = "楼栋均价")
    private String buildprice;

    @ApiModelProperty(value = "排序")
    private String orderby;

    @ApiModelProperty(value = "楼栋状态")
    private String status;

    @ApiModelProperty(value = "是否删除")
    private String isdel;

    @ApiModelProperty(value = "创建时间")
    private String createtime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改时间")
    private String edittime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "单元集合")
    private List<BldUnit> unitList;

    private List<String> checkFloor;
}
