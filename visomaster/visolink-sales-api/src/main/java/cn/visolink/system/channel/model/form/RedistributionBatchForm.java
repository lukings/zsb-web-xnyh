package cn.visolink.system.channel.model.form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author:
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/9/7
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_redistribution_batch")
@ApiModel(value = "RedistributionBatch对象", description = "重分配批次")
public class RedistributionBatchForm extends Page {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "分配原因")
    @TableId(value = "RedistributionType")
    private String redistributionType;

    @ApiModelProperty(value = "操作人")
    @TableId(value = "createUser")
    private String createUser;

    @ApiModelProperty(value = "操作时间")
    @TableId(value = "createTime")
    private String createTime;

    @ApiModelProperty(value = "涉及数量")
    @TableId(value = "countNumber")
    private String countNumber;

    @ApiModelProperty(value = "项目id")
    @TableId(value = "projectId")
    private String projectId;

    @ApiModelProperty(value = "备注")
    @TableId(value = "note")
    private String note;

    @ApiModelProperty(value = "重分配类型 （1.拓客台账 2.案场台账 3.app 4.公共池）")
    @TableId(value = "RedistributionGenre")
    private String redistributionGenre;


}
