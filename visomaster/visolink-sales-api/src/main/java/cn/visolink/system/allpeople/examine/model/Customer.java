package cn.visolink.system.allpeople.examine.model;

import lombok.Data;

/**
 * 推荐客户
 * @Auther: wang gang
 * @Date: 2020/1/31 17:05
 * @Description: Pointing to the breeze, the procedure is self-contained
 */
@Data
public class Customer {

    private int num;//序号

    private String CustomerName;//客户姓名

    private String CustomerMobile;//客户手机号

    private String ProjectName;//项目名称

    private String CurrentState;//当前状态

    private String Zygw;//置业顾问名称

    private String ZygwMobile;//置业顾问手机号

    private String ReportCreateTime;//报备时间

    private String TheFirstVisitDate;//首访时间

    private String ContractDate;//签约时间

    private String ExpireDate;//失效时间
}
