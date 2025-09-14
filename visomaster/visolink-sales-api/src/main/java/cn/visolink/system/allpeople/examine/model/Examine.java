package cn.visolink.system.allpeople.examine.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName Examine
 * @Author wanggang
 * @Description //经纪人信息
 * @Date 2020/1/14 19:08
 **/
@Data
public class Examine {

    private int num;//序号

    private String id;//经纪人ID

    private String mobile;//手机号

    private String mobileAll;//手机号

    private String name;//姓名

    private String gender;//性别

    private String imgUrl;//头像

    private String currentRole;//当前身份

    private String oaaccount;//OA账号

    private String roleID;//身份ID

    private String RegistFrom;//注册来源

    private String RegistRefcommend;//注册推荐信息

    private String RegistWeChatUserName;//推荐人昵称

    private String RegistProject;//注册项目/区域/集团

    private String roleName;//身份名称

    private String idcard;//身份证

    private String FrontImgUrl;//身份证正面照片

    private String ReverseImgUrl;//身份证反面照片

    private String status;//状态（1.启用0.禁用2.待审核3.审核驳回）

    private String IsDel;//是否删除（0.未删除，1.删除）

    private String createTime;//创建时间

    private String ExamineTime;//审核时间

    private String accontName;//绑定旭客汇账号信息

    private String SharePage;//分享页面

    private String BookPosterId;//关联海报ID

    private String RegistModuleDataId;//注册模块数据id

    private String BrokerLevel;//经纪人推荐级别

    private int CustomerCnt = 0;//推荐客户数量

    private int VisitCnt = 0;//到访数量

    private int SubCntCount = 0;//认购套数

    private int SubCnt = 0;//认购客户数量(导出时即认购套数)

    private int DealCntCount = 0;//签约套数

    private int DealCnt = 0;//签约客户数量(导出时即签约套数)

    private int InvalidCnt = 0;//失效数量

    private String OrderAmt = "0.00";//认购金额

    private String CntrtAmt = "0.00";//签约金额

    private String companyID;//所属公司ID

    private String companyName;//所属公司

    /**
     * 根据条件，或者全号，或引号数据
     * @param isAllPhone 是否获取全号
     * @return
     */
    private String getCustomerMobile(boolean isAllPhone) {
        String phone = getMobile();
        if (isAllPhone) {
            phone = getMobileAll();
        }
        return phone;
    }
    /**
     * 获取数据
     * @param isAllPhone
     * @return
     */
    public Object[] toPublicData(boolean isAllPhone){
        String phone = getCustomerMobile(isAllPhone);
        return new Object[]{
                getNum(),getName(),getRoleName(),getCompanyName(),phone,
                getCreateTime(),getStatus()
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "序号","姓名","身份","所属公司",
            "手机号码","注册时间","状态"};

}
