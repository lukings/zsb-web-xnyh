package cn.visolink.system.invalidvisit.model;

import lombok.Data;

/**
 * @ClassName InvalidVisit
 * @Author wanggang
 * @Description //无效到访客户
 * @Date 2020/3/10 9:48
 **/
@Data
public class InvalidVisit {

    private int num;

    private String ID;

    /**
     * 项目id
     */
    private String ProjectId;
    /**
     * 项目名称
     */
    private String ProjectName;
    /**
     * 来访人数
     */
    private String VisitNum;
    /**
     * 来访原因code
     */
    private String VisitReasonCode;
    /**
     * 来访原因
     */
    private String VisitReason;
    /**
     * 创建时间
     */
    private String CreateTime;
    /**
     * 创建人Id
     */
    private String CreateUserId;
    /**
     * 创建人姓名
     */
    private String CreateUserName;
    /**
     * 来访时间
     */
    private String VisitTime;

    public String[]  toInvalidVisitTitle =  new String[]{
            "序号","项目","来访时间",
            "来访人数","来访原因","来访备注","创建时间",
            "操作人"};

    /**
     * 获取来访的数据
     * @param
     * @return
     */
    public Object[] toInvalidVisitData(){
        return new Object[]{
                getNum(),getProjectName(),getVisitTime(),getVisitNum(),getVisitReasonCode(),getVisitReason(),
                getCreateTime(),getCreateUserName()
        };
    }
}
