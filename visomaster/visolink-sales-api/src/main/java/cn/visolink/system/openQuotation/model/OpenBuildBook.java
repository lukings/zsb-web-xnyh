package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OpenBuildBook
 * @Author wanggang
 * @Description //开盘对应楼盘
 * @Date 2021/1/4 9:52
 **/
@Data
public class OpenBuildBook  implements Serializable {

    private String id;

    private String projectId;

    private String activityId;

    private String bookId;

    private String creator;
}
