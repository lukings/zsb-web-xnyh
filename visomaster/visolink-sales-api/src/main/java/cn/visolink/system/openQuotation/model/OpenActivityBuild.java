package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OpenActivityBuild
 * @Author wanggang
 * @Description //开盘楼栋
 * @Date 2021/1/4 9:43
 **/
@Data
public class OpenActivityBuild  implements Serializable {

    private String id;

    private String projectId;

    private String projectName;

    private String activityId;

    private String buildId;

    private String buildName;

    private String creator;

    private String createTime;

}
