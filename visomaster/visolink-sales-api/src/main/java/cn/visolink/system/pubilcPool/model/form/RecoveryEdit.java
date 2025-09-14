package cn.visolink.system.pubilcPool.model.form;

import cn.visolink.system.pubilcPool.model.PublicPoolVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName RecoveryEdit
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/28 17:15
 **/
@Data
@ApiModel(value = "公共池对象", description = "公共池对象")
public class RecoveryEdit {

    @ApiModelProperty(value = "项目ID")
    private String projectId;
    @ApiModelProperty(value = "成交渠道")
    private String sourceType;
    @ApiModelProperty(value = "池类型（1 公共池 2 淘客池）")
    private String poolType;
    @ApiModelProperty(value = "客户ID")
    private List<String> cstId;
    @ApiModelProperty(value = "线索ID")
    private List<String> clueIds;
    @ApiModelProperty(value = "客户手机号")
    private List<String> mobiles;
    @ApiModelProperty(value = "报备人ID")
    private String allocationUserStr;
    @ApiModelProperty(value = "备注")
    private String doDesc;
    @ApiModelProperty(value = "重分配原因")
    private String reason;
    @ApiModelProperty(value = "当前登陆人姓名")
    private String userName;
    @ApiModelProperty(value = "修改人")
    private String editor;
    @ApiModelProperty(value = "客户状态")
    private String clueStatus;

}
