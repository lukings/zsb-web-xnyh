package cn.visolink.system.company.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName CompanyExport
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/19 15:37
 **/
@Data
@ApiModel(value="CompanyExport对象", description="")
public class CompanyExport implements Serializable {

    @ApiModelProperty(value = "序号")
    private String rowNum;

    @ApiModelProperty(value = "门店机构代码")
    private String companyCode;

    @ApiModelProperty(value = "门店全称")
    private String companyName;

    @ApiModelProperty(value = "关联公司")
    private String headquartersName;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "修改时间")
    private String editTime;
    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "是否启用")
    private String status;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "关联项目")
    private String projectNames;

    @ApiModelProperty(value = "门店展示名称")
    private String orgName;



    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toData() {
        return new Object[]{
                getRowNum(), getHeadquartersName(), getCompanyName(), getOrgName(),getCompanyCode(), getStartTime(),
                getEndTime(), getStatus(), getCreator(), getCreateTime(), getEditTime(), getProjectNames()
        };
    }

    public String[] courtCaseTitle = new String[]{
            "序号", "关联公司","门店全称","门店展示名称","门店机构代码","有效开始时间", "有效结束时间", "是否启用",
            "创建人", "创建时间", "更新时间", "关联项目"};
}
