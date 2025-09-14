package cn.visolink.system.job.authorization.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Mr.Yu
 * @Date: 2021/10/11 16:51
 * @description
 * @Version 1.0
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BindProject implements Serializable {

    /**
     *   主键id
     */
    private Integer id;
    
    /**
     *   项目id
     */
    private String projectId;
    
    /**
     *   项目名称
     */
    private String projectName;
    
    /**
     * 用于自动生成的项目名称
     */
    private String generateName;
    
    /**
     *   项目开始时间
     */
    private Date startTime;
    
    /**
     *   项目结束时间
     */
    private Date endTime;
    
    
}

