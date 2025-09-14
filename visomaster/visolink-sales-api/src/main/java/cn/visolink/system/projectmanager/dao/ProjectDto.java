package cn.visolink.system.projectmanager.dao;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/11 16:24
 * @description
 * @Version 1.0
 */
@Data
public class ProjectDto implements Serializable {

    private String projectName;
    private Integer status;
    private String creator;
    private String id;
    private String orgId;
    private String areaId;
    private String areaName;
    private String createTime;

}

