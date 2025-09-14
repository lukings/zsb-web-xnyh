package cn.visolink.system.allpeople.examine.model;

import lombok.Data;

/**
 * 用户变更日志
 * @Auther: wang gang
 * @Date: 2020/2/3 16:14
 * @Description: Pointing to the breeze, the procedure is self-contained
 */
@Data
public class UserEdit {
    private int num;
    private String BrokerId;//经纪人ID
    private String BrokerName;//现经纪人名称
    private String EditField;//变更字段
    private String BeforeChange;//变更前数据
    private String AfterAlteration;//变更后数据
    private String CreateTime;//创建时间
    private String Creator;//创建人
    private String EditTime;//修改时间
    private String Editor;//修改人
}
