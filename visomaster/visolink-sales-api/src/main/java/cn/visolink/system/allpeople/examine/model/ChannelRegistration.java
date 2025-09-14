package cn.visolink.system.allpeople.examine.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ChannelRegistration
 * @Author wanggang
 * @Description //渠道商列表
 * @Date 2022/4/1 13:58
 **/
@ApiModel(value="渠道商列表", description="渠道商列表")
@Data
public class ChannelRegistration {

    @ApiModelProperty(value = "序号")
    private String rownum;
    @ApiModelProperty(value = "渠道商id")
    private String id;
    @ApiModelProperty(value = "公司属性:1--个人 2--公司")
    private String companyAttr;
    @ApiModelProperty(value = "渠道商名称")
    private String companyName;
    @ApiModelProperty(value = "渠道商Code")
    private String companyCode;
    @ApiModelProperty(value = "渠道商手机号")
    private String companyMobile;
    @ApiModelProperty(value = "渠道商关联项目")
    private String projectNames;
    @ApiModelProperty(value = "申请时间")
    private String createTime;
    @ApiModelProperty(value = "申请人")
    private String handler;
    @ApiModelProperty(value = "审核状态（1：待审核 2：审核通过 3：拒绝）")
    private String examineStatus;
    @ApiModelProperty(value = "申请人手机号")
    private String handleMobile;
    @ApiModelProperty(value = "营业执照编号")
    private String businessLicenseNo;
    @ApiModelProperty(value = "渠道商资质")
    private String qualification;
    @ApiModelProperty(value = "渠道商成立日期")
    private String incorporationTime;
    @ApiModelProperty(value = "法人名称")
    private String legalPerson;
    @ApiModelProperty(value = "渠道商注册地址")
    private String companyAddress;
    @ApiModelProperty(value = "经办人身份证号")
    private String handlerCardNo;
    @ApiModelProperty(value = "法人身份证号")
    private String legalCardNo;
    @ApiModelProperty(value = "身份证人像面路径")
    private String legalCardPortraitUrl;
    @ApiModelProperty(value = "身份证国徽面路径")
    private String legalCardEmblemUrl;
    @ApiModelProperty(value = "营业执照路径")
    private String businessLicenseUrl;
    @ApiModelProperty(value = "是否黑名单")
    private String isBlock;
    @ApiModelProperty(value = "经办人身份证人像面路径")
    private String handlerCardPortraitUrl;
    @ApiModelProperty(value = "经办人身份证国徽面路径")
    private String handlerCardEmblemUrl;
    @ApiModelProperty(value = "银行卡号")
    private String bankCardNo;
    @ApiModelProperty(value = "户名")
    private String bankCardUserName;
    @ApiModelProperty(value = "开户行名称")
    private String bankOfDeposit;
    @ApiModelProperty(value = "增值税率")
    private String valueAddedTaxRate;
    @ApiModelProperty(value = "社会统一征信编码")
    private String creditCode;
    @ApiModelProperty(value = "拒绝原因")
    private String rejectionReason;
    @ApiModelProperty(value = "合同名称")
    private String contractName;
    @ApiModelProperty(value = "合同ID")
    private String contractId;
    @ApiModelProperty(value = "合同状态")
    private String contractStatus;
    @ApiModelProperty(value = "合同创建时间")
    private String conCreateTime;

    @ApiModelProperty(value = "渠道商合同列表")
    private List<ChannelContract> channelContracts;

    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toData1(){
        return new Object[]{
                getRownum(),getCompanyName(),getCompanyCode(),getIsBlock(),getContractName(),getContractStatus(),
                getCreateTime(),getHandler()
        };
    }
    public String[]  courtCaseTitle1 =  new String[]{
            "序号","渠道商名称","渠道商机构代码","是否黑名单","合同名称","合同状态",
            "申请时间","申请人"};

    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toData2(){
        return new Object[]{
                getRownum(),getCompanyAttr(),getCompanyName(),getCreateTime(),getHandler(),
                getExamineStatus()
        };
    }
    public String[]  courtCaseTitle2 =  new String[]{
            "序号","渠道商类型","渠道商名称","申请时间",
            "申请人","状态"};
}
