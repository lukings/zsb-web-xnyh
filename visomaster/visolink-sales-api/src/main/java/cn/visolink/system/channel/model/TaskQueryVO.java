package cn.visolink.system.channel.model;

import cn.visolink.common.PageQuery;
import lombok.Data;


@Data
public class TaskQueryVO extends PageQuery {
    /**
     * 主键
     */
    private String taskId;

}
