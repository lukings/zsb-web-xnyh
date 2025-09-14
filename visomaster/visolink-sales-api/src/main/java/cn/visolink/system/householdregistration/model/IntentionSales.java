package cn.visolink.system.householdregistration.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName IntentionSales
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/13 10:18
 **/
@Data
@ApiModel(value="装户结果置业顾问维度", description="装户结果置业顾问维度")
public class IntentionSales implements Serializable {

    @ApiModelProperty(value = "置业顾问id")
    private String salesId;
    @ApiModelProperty(value = "置业顾问id")
    private String salesName;
    @ApiModelProperty(value = "装户客户")
    private List<IntentionCst> csts;
}
