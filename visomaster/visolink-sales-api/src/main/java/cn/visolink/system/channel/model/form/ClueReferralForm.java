package cn.visolink.system.channel.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ClueReferralForm
 * @Author wanggang
 * @Description //线索转介查询辅助
 * @Date 2022/3/30 9:11
 **/
@Data
@ApiModel(value = "ClueReferralForm对象", description = "线索转介查询辅助")
public class ClueReferralForm {

    @ApiModelProperty(value = "客户姓名")
    private String cstName;
    @ApiModelProperty(value = "客户手机号")
    private String mobile;
    @ApiModelProperty(value = "项目ID集合")
    private List<String> proIds;
    @ApiModelProperty(value = "当前页")
    private String current;
    @ApiModelProperty(value = "每页行数")
    private String size;
    @ApiModelProperty(value = "转介类型（1：渠道转介 2：业务员转机会）")
    private String referralType;


}
