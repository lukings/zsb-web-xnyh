package cn.visolink.system.channel.model;

import lombok.Data;

/**
 * @author luqianqian
 * @Description: 客户跟进交易日志记录表
 * @date 2024/11/10 20:21
 */
@Data
public class CustomerFodLog {

    private String id;
    private String opportunityClueId;
    private String busuinessId;
    private String busuinessType;
    private String businessTime;
    private String projectId;
    private String mainProjectId;
}
