package cn.visolink.system.householdregistration.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName BldUnit
 * @Author wanggang
 * @Description //单元
 * @Date 2020/7/29 14:12
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="单元", description="单元")
public class BldUnit implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "项目ID")
    private String projectid;

    @ApiModelProperty(value = "活动ID")
    private String activityId;


    @ApiModelProperty(value = "楼栋ID")
    private String buildguid;

    @ApiModelProperty(value = "单元ID")
    private String bldunitguid;

    @ApiModelProperty(value = "单元编号")
    private String unitno;

    @ApiModelProperty(value = "单元名称")
    private String unitname;

    @ApiModelProperty(value = "单元楼层房间最大数")
    private int maxroomcount;

    @ApiModelProperty(value = "是否删除（1已删除，0未删除）")
    private String isdel;

    @ApiModelProperty(value = "创建时间")
    private String createtime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改时间")
    private String edittime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "房间集合")
    private List<IntentionPlaceRoom> roomList;

    private List<String> checkArr;
}
