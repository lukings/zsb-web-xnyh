package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author wanggang
 * @Description 打卡统计VO
 * @Date 2025/1/27
 **/
@Data
@ApiModel(value = "打卡统计VO")
public class PunchInStatisticsVO {

    @ApiModelProperty(value = "汇总数据列表")
    private List<PunchInSummaryVO> summaryList;

    @ApiModelProperty(value = "明细数据列表")
    private List<PunchInDetailVO> detailList;

    @ApiModelProperty(value = "当前页码")
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

    @ApiModelProperty(value = "总记录数")
    private Integer total;

    /**
     * 打卡统计汇总VO
     */
    @Data
    @ApiModel(value = "打卡统计汇总VO")
    public static class PunchInSummaryVO {
        @ApiModelProperty(value = "组织ID")
        private String orgId;

        @ApiModelProperty(value = "组织名称")
        private String orgName;

        @ApiModelProperty(value = "项目ID")
        private String projectId;

        @ApiModelProperty(value = "项目名称")
        private String projectName;

        @ApiModelProperty(value = "打卡人数")
        private Integer punchInUserCount;

        @ApiModelProperty(value = "打卡客户数")
        private Integer punchInCustomerCount;

        @ApiModelProperty(value = "打卡次数")
        private Integer punchInCount;
    }

    /**
     * 打卡统计明细VO
     */
    @Data
    @ApiModel(value = "打卡统计明细VO")
    public static class PunchInDetailVO {
        @ApiModelProperty(value = "组织ID")
        private String orgId;

        @ApiModelProperty(value = "组织名称")
        private String orgName;

        @ApiModelProperty(value = "项目ID")
        private String projectId;

        @ApiModelProperty(value = "项目名称")
        private String projectName;

        @ApiModelProperty(value = "客户线索ID")
        private String projectClueId;

        @ApiModelProperty(value = "客户名称")
        private String customerName;

        @ApiModelProperty(value = "销售归属ID")
        private String salesAttributionId;

        @ApiModelProperty(value = "销售归属名称")
        private String salesAttributionName;

        @ApiModelProperty(value = "销售归属团队ID")
        private String salesAttributionTeamId;

        @ApiModelProperty(value = "销售归属团队名称")
        private String salesAttributionTeamName;

        @ApiModelProperty(value = "跟进记录ID")
        private String followUpId;

        @ApiModelProperty(value = "打卡时间")
        private Date punchInTime;

        @ApiModelProperty(value = "打卡人")
        private String punchInUser;

        @ApiModelProperty(value = "打卡人ID")
        private String punchInUserId;
    }
}
