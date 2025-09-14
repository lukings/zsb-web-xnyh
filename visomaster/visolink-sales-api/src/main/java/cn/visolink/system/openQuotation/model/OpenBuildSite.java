package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OpenBuildSite
 * @Author wanggang
 * @Description //概览图坐标
 * @Date 2021/1/4 10:05
 **/
@Data
public class OpenBuildSite  implements Serializable {

    private String id;

    private String projectid;

    private String activityId;

    private String materialid;

    private String projectname;

    private String buildname;

    private String buildguid;

    private String buildsiteX;

    private String buildsiteY;
}
