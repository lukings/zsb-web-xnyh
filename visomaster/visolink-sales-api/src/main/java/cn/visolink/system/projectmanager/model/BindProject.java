package cn.visolink.system.projectmanager.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Mr.Yu
 * @Date: 2021/10/11 9:56
 * @description
 * @Version 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BindProject implements Serializable {

    /**
     *   主键id
     */
    private Integer id;

    /**
     *   uuid
     */
    private String uuid;

    /**
     *   是否绑定过 0:未绑定 1: 绑定
     */
    private Integer isBind;

    /**
     *   项目id
     */
    private String projectId;

    /**
     *   项目名称
     */
    private String projectName;

    /**
     *   是否删除 0: 不删除 1: 删除
     */
    private Integer isDel;

    /**
     *   是否启用 0: 不启用 1: 启用
     */
    private Integer status;

    /**
     *   创建时间
     */
    private Date createTime;

    /**
     *   修改时间
     */
    private Date editTime;

    /**
     *   项目开始时间
     */
    private Date startTime;

    /**
     *   项目结束时间
     */
    private Date endTime;

    /**
     *   公司编码
     */
    private String companyCode;

    /**
     *   公司名称
     */
    private String companyName;

    /**
     *   创建者
     */
    private String creator;

    /**
     *   修改者
     */
    private String editor;
}