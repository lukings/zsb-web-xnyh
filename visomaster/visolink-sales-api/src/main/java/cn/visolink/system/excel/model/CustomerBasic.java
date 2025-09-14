package cn.visolink.system.excel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Data;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>
 * CustomerBasicForm对象
 * </p>
 *
 * @author autoJob
 * @since 2019-09-26
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "CustomerBasic对象", description = "客户表")
public class CustomerBasic {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "姓名")
    private String Name;

    @ApiModelProperty(value = "性别")
    private Boolean Gender;

    @ApiModelProperty(value = "电话")
    private String Mobile;

    @ApiModelProperty(value = "客户性质 0:个人 1:公司")
    private Boolean CustomerType;

    @ApiModelProperty(value = "年龄信息")
    private String AgeInfo;

    @ApiModelProperty(value = "家庭住址")
    private String Address;

    @ApiModelProperty(value = "证件类型")
    private String CardType;

    @ApiModelProperty(value = "证件号")
    private String CardNum;

    @ApiModelProperty(value = "标签")
    private String Tag;

    @ApiModelProperty(value = "认证公司")
    private String AuthCompanyID;

    @ApiModelProperty(value = "组织归属")
    private String OrgID;

    @ApiModelProperty(value = "产品ID")
    private String ProductID;

    @ApiModelProperty(value = "创建人")
    private String Creator;

    @ApiModelProperty(value = "创建时间")
    private String CreateTime;

    @ApiModelProperty(value = "编辑人")
    private String Editor;

    @ApiModelProperty(value = "编辑时间")
    private String EditeTime;

    @ApiModelProperty(value = "状态")
    private Boolean Status;

    @ApiModelProperty(value = "是否删除")
    private Boolean IsDel;

    @ApiModelProperty(value = "是否业主")
    private Integer isOwner;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "刷新数据标识")
    private String flag;

    @ApiModelProperty(value = "项目名称")
    private String ProjectName;


}
