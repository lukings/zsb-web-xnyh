package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OpenNotRoom
 * @Author wanggang
 * @Description //不可设置房间
 * @Date 2021/1/4 9:46
 **/
@Data
public class OpenNotRoom  implements Serializable {

    private String id;

    private String projectId;

    private String projectName;

    private String activityId;

    private String buildId;

    private String roomId;

    private String roomState = "0";

    private String creator;

    private String createTime;
}
