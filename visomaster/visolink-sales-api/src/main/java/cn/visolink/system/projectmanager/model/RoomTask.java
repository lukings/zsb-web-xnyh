package cn.visolink.system.projectmanager.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 孙林
 * @date:2019-9-10
 */
@ToString
@Data
@Accessors(chain = true)
public class RoomTask implements Serializable {
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 申请名称
     */
    private String applyName;

    /**
     * 申请日期
     */
    private String applyTime;

    /**
     * 状态 0：草稿 1：已执行
     */
    private Integer status;

    /**
     * 调整套数
     */
    private Integer adjustNum;

    /**
     * 调整前预售建筑面积
     */
    private BigDecimal beforeYsBldArea;

    /**
     * 调整前预售套内面积
     */
    private BigDecimal beforeYsTnArea;

    /**
     * 调整前实测建筑面积
     */
    private BigDecimal beforeScBldArea;

    /**
     * 调整前实测套内面积
     */
    private BigDecimal beforeScTnArea;

    /**
     * 调整后预售建筑面积
     */
    private BigDecimal afterYsBldArea;

    /**
     * 调整后预售套内面积
     */
    private BigDecimal afterYsTnArea;

    /**
     * 调整后实测建筑面积
     */
    private BigDecimal afterScBldArea;

    /**
     * 调整后实测套内面积
     */
    private BigDecimal afterScTnArea;

    /**
     * 经办人
     */
    private String agent;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 创建人账号
     */
    private String createUser;

    /**
     * 修改人账号
     */
    private String updateUser;

    /**
     * 是否删除 0：正常；1：删除
     */
    private Byte isDelete;

    /**
     * 备注
     */
    private String remark;

    /**
     *   行
     */
    private Integer row;

}