package cn.visolink.system.project.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目联动项目表
 * @TableName b_project_translate
 */
@Data
public class TranslateProjectVo implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 联动项目id
     */
    private String transProjectId;

    /**
     * 联动项目名称
     */
    private String transProjectName;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 联动开始时间
     */
    private String startTime;

    /**
     * 联动结束时间
     */
    private String endTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String creatTime;

    /**
     * 更新人
     */
    private String editor;

    /**
     * 更新时间
     */
    private String enitTime;

    /**
     * 是否删除
     */
    private Integer isdel;

    /**
     * 用户id
     */
    private String userId;

    private static final long serialVersionUID = 1L;
}