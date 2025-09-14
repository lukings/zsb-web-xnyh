package cn.visolink.system.parameter.model.vo;/**
 * @className: ProjectBankVO
 * @description: TODO
 * @author: yhx
 * @date: 2021/1/11 16:16
 * @version: 1.0
 **/

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2021/1/11 16:16
 */
@Data
@ApiModel(value = "项目商户配置")
public class ProjectBank {

    @ApiModelProperty("项目分期ID")
    private String projectFid;

    private String projectId;

    private Integer pageSize;

    private Integer pageNum;

    @ApiModelProperty("分期名称")
    private String projectStageName;

    @ApiModelProperty("商户号")
    private String proCollAccount;

    @ApiModelProperty("所属银行")
    private String collBank;

    @ApiModelProperty("银行卡号")
    private String bankNo;

    @ApiModelProperty("金蝶编码")
    private String kingdeeBankNo;

    @ApiModelProperty("创建人ID")
    private String userId;

    @ApiModelProperty("主键ID")
    private String id;

    private Integer status;
}
