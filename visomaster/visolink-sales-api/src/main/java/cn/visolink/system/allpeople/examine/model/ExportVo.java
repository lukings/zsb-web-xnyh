package cn.visolink.system.allpeople.examine.model;

import lombok.Data;

/**
 * @ClassName ExportVo
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/30 14:34
 **/
@Data
public class ExportVo {

    private String regProjectID;//注册项目/区域/集团

    private String roleName;//经纪人身份

    private String Status;//经纪人状态

    private String search;//姓名、手机号

    private String isAllPhone;//是否全号

    private String beginTime;//开始时间

    private String endTime;//结束时间

    private String RegistFroms;//注册来源

    private String recommend;//推荐人姓名

    private String weChatUserName;//推荐人昵称

    private String noReg;//是否注册项目

    private String isOA;//是否绑定OA账号

    private String userName;//当前登录账号

    private String userId;//当前登录账号ID

    private String projectId;//推荐项目

    private String brokerLevel;//经纪人注册级别

    private String authenticationStatus;//是否认证通过

    private String companycode;//公司编码

    private String companyName;//公司编码
}
