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

/**
 * @ClassName IntentionPlaceMaterial
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/7/29 20:52
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("a_intention_place_build")
@ApiModel(value="装户活动素材", description="装户活动素材")
public class IntentionPlaceMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "装户活动id")
    private String activityId;

    @ApiModelProperty(value = "素材类型（1活动中心封面2概览图配置）")
    private String materialType;

    @ApiModelProperty(value = "素材链接地址")
    private String materialAddress;

    @ApiModelProperty(value = "概览图路径")
    private String endPhotoUrl;

    @ApiModelProperty(value = "排序")
    private String orderby;

    @ApiModelProperty(value = "状态")
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
}
