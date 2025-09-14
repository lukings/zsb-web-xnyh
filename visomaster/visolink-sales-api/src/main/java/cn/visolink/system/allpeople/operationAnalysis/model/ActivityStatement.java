package cn.visolink.system.allpeople.operationAnalysis.model;

import lombok.Data;

/**
 * @ClassName ActivityStatement
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/7/7 14:16
 **/
@Data
public class ActivityStatement {

    private String rownum;//序号
    private String AreaName;//区域
    private String AreaNames;//区域字符串
    private String ProjectNums;//项目编码
    private String ProjectName;//项目
    private String ActivityID;//活动ID
    private String ActivityName;//活动名称
    private String ActivityBeginTime;//活动开始时间
    private String ActivityEndTime;//活动结束时间
    private String CreateTime;//活动创建时间
    private String ReleaseTime;//活动发布时间
    private String Status;//活动状态
    private String CouponCollected;//领券数
    private String ActivityType;//活动类型
    private String SignUpCount;//报名数
    private String SignInCount;//签到数
    private String NeedHelpCount;//发起助力人数
    private String HelpCount;//助力人数
    private String NewVisitCount;//活动期间新访数
    private String ReVisitCount;//活动期间复访数
    private String VisitCount = "0";//活动实际到访数
    private String OrderCount = "0";//活动报备认购套数
    private String ContractCount = "0";//活动报备签约套数
    private String vowCount;

    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toActivityData(){
        return new Object[]{
                getRownum(),getActivityName(),getProjectName(),getProjectNums(),getActivityBeginTime(),
                getActivityEndTime(),getCreateTime(),getReleaseTime(),
                getStatus(),getActivityType(),getCouponCollected(),getVowCount(),
                getSignUpCount(),getSignInCount(),getNeedHelpCount(),getHelpCount(),
                getNewVisitCount(),getReVisitCount(),getVisitCount(),getOrderCount(),getContractCount()
        };
    }

    public String[]  activityTitle =  new String[]{
            "序号","活动名称","项目","项目编码","活动开始时间","活动截止时间",
            "活动创建时间","活动发布时间","活动状态","活动类型",
            "领券数","许愿人数","报名数","签到数","发起助力人数","助力人数",
            "活动期间新访数","活动期间复访数","活动到访数","活动认购套数","活动签约套数"};


}
