package cn.visolink.system.job.authorization.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2021/4/25
 */
@Data
public class BrokerAccountRecordsBatch implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id; //主键id
    private String reason; //分配原因
    private String createUser; //操作人
    private String createTime; // 操作时间
    private String countNumber;//涉及数量
    private String projectId;//项目id
    private String note;//备注
    private String entrance;//重分配入口
    private String message;//消息文案
    private String accountId;//大客户经理
}
