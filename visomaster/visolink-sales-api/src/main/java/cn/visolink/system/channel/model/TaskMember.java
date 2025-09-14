package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务-团员中间表
 * @TableName b_task_member
 */
@Data
public class TaskMember implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 团员id
     */
    private String memberId;

    /**
     * 团员姓名
     */
    private String memberName;


    /**
     * 团员已拓客（团员总报备）
     */
    private Integer memberReport;

    /**
     * 报备已拓客
     */
    private Integer reportCount;

    /**
     * 拜访已拓客
     */
    private Integer visitCount;

    /**
     * 到访已拓客
     */
    private Integer arriveCount;

    /**
     * 三个一已拓客
     */
    private Integer threeOneCount;
    
    
    /**
     * 成交
     */
    private Integer dealCount;

    /**
     * 首访
     */
    private Integer firstVisitCount;

    /**
     * 复访
     */
    private Integer repeatVisitCount;

    /**
     * 标记量
     */
    private Integer tagCount;



    private static final long serialVersionUID = 1L;
    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("修改人")
    private String modifyBy;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("修改时间")
    private String modifyTime;
}