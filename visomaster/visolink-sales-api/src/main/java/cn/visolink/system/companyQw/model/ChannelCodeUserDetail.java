package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ChannelCodeUserDetail
 * @Author wanggang
 * @Description //渠道码客户明细
 * @Date 2022/2/8 22:11
 **/
@Data
@ApiModel(value = "渠道码客户明细", description = "渠道码客户明细")
public class ChannelCodeUserDetail {

    @ApiModelProperty(value = "序号")
    private String rownum;

    @ApiModelProperty(value = "员工姓名")
    private String userName;

    @ApiModelProperty(value = "员工手机号")
    private String userMobile;

    @ApiModelProperty(value = "总客户数量")
    private String sumCount;

    @ApiModelProperty(value = "新增客户数")
    private String newCount;

    @ApiModelProperty(value = "流失客户数")
    private String delCount;

    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toChannelCodeData() {
        return new Object[]{
                getRownum(),getUserName(),getUserMobile(),getSumCount(),getNewCount(),
                getDelCount()
        };
    }

    public String[] channelCodeTitle = new String[]{
            "序号","成员姓名","成员手机号","累计客户数","新增客户数","流失客户数"};

}
