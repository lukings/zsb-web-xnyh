package cn.visolink.system.channel.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author autoJob
 * @since 2019-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_publicpool")
@ApiModel(value="Publicpool对象", description="")
public class Publicpool implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @TableField("ClueID")
    private String clueId;

    @TableField("Name")
    private String name;

    @TableField("Gender")
    private Boolean gender;

    @TableField("Level")
    private String level;

    @TableField("OperationTime")
    private Date operationTime;

    @TableField("TheFirstVisitDate")
    private Date theFirstVisitDate;

    @TableField("ClueSource")
    private String clueSource;

    @TableField("ReportUserName")
    private String reportUserName;

    @TableField("ReportTime")
    private Date reportTime;

    @TableField("SaleID")
    private String saleId;

    @TableField("SaleName")
    private String saleName;

    @TableField("ExpireTag")
    private String expireTag;

    @TableField("CatchTime")
    private Date catchTime;

    @TableField("CatchWay")
    private String catchWay;

    @TableField("ClueStatus")
    private Boolean clueStatus;

    @TableField("Mobile")
    private String mobile;

    @TableField("ProjectID")
    private String projectId;

    @TableField("SourceType")
    private Boolean sourceType;

    @ApiModelProperty(value = "1 报备过期2 跟进过期3 到访过期4 丢弃5 顾问离职6 报备人离职")
    @TableField("Reason")
    private Boolean reason;


}
