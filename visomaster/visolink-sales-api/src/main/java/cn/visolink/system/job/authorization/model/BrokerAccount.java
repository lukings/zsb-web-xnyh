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
public class BrokerAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String brokerId;//二级经纪人ID
    private String accountId;//大客户经理ID
    private String projectId;//归属项目ID
    private String projectName;//归属项目名称
    private String isDel;//是否删除1删除，0未删除
    private String createTime;//创建时间
    private String editTime;//编辑时间
}
