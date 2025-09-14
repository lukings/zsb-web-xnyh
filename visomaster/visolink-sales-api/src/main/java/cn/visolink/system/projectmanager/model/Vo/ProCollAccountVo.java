package cn.visolink.system.projectmanager.model.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ProCollAccountVo
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/12/6 14:20
 **/
@Data
@ApiModel(value="收款账号")
public class ProCollAccountVo{

    @ApiModelProperty(value = "序号")
    private String rownum;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "新增或编辑（1：新增 2：编辑）")
    private String addOrEdit;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目")
    private String projectName;

    @ApiModelProperty(value = "分期ID")
    private String projectFid;

    @ApiModelProperty(value = "金蝶银行编码")
    private String kingdeeBankNo;

    @ApiModelProperty(value = "创建人ID")
    private String creator;

    @ApiModelProperty(value = "创建人时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String updator;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "0禁用 1启用")
    private String status;

    @ApiModelProperty(value = "收款账户类型")
    private String accountType;

    @ApiModelProperty(value = "商户号")
    private String proCollAccount;

    @ApiModelProperty(value = "收款银行")
    private String collBank;

    @ApiModelProperty(value = "银行卡号")
    private String bankNo;

    @ApiModelProperty(value = "银行卡用户姓名")
    private String bankCstName;

    @ApiModelProperty(value = "银行卡用户预留手机号")
    private String bankCstMobile;

    @ApiModelProperty(value = "近一次结算是否正常(1:正常 2：异常)")
    private String isOk;

    @ApiModelProperty(value = "签署协议时间（小时）")
    private String signingTime;

    @ApiModelProperty(value = "原商户号")
    private String oldProCollAccount;

    @ApiModelProperty(value = "原收款银行")
    private String oldCollBank;

    @ApiModelProperty(value = "原银行卡号")
    private String oldBankNo;

    @ApiModelProperty(value = "原银行卡用户姓名")
    private String oldBankCstName;

    @ApiModelProperty(value = "原银行卡用户预留手机号")
    private String oldBankCstMobile;

    @ApiModelProperty(value = "原收款账户类型")
    private String oldAccountType;

    @ApiModelProperty(value = "原签署协议时间（小时）")
    private String oldSigningTime;

    @ApiModelProperty(value = "验证码")
    private String xCode;

    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toProCollData() {
        return new Object[]{
                getRownum(),getProjectName(),getAccountType(),getProCollAccount(),getIsOk(),
                getCollBank(),getBankNo(),getBankCstName(),getBankCstMobile()
        };
    }

    public String[] proCollTitle = new String[]{
            "序号","项目","收款类型","第三方账号",
            "近一次结算是否正常","银行类型","银行卡号","银行卡所属人姓名","银行预留手机号"};

}
