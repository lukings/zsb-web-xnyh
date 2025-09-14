package cn.visolink.system.channel.model;

import lombok.Data;

import java.util.List;

/**
 * @author luqianqian
 * @Description: 客户报备日志记录表
 * @date 2024/11/10 20:15
 */
@Data
public class CustomerAddLog {

    private String id;
    private String areaId;
    private String projectId;
    private String opportunityClueId;
    private String isAdd;
    private String reportCreateTime;
    private List<CustomerFodLog> customerFodLogList;
}
