package cn.visolink.system.allpeople.operationAnalysis.model;

import lombok.Data;

/**
 * @ClassName ExportVo
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/4/14 19:37
 **/
@Data
public class ExportVo {

    private String cityIds;

    private String projectIds;

    private String beginTime;

    private String endTime;

    private String userName;

    private String userId;

    private String reportTime;

    private String companycode;//公司编码
}
