package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

import java.util.List;

/**
 * 反馈
 * @Auther: wang gang
 * @Date: 2020/2/1 09:31
 * @Description: Pointing to the breeze, the procedure is self-contained
 */
@Data
public class Feedback {

    private int num;//序号

    private String ID;//ID

    private String FeedBackUserId;//反馈人ID

    private String FeedBackUserName;//反馈人名称

    private String FeedBackUserMobile;//反馈人手机隐号

    private String oldFeedBackUserMobile;//反馈人手机全号

    private String ProblemDesc;//问题描述

    private String FeedBackUserRole;//反馈人角色

    private String ImgUrl;//问题截图链接

    private String HandleUserId;//处理人ID

    private String HandleUserName;//处理人名称

    private String HandleTime;//处理时间

    private String HandleDesc;//处理说明

    private String Creator;//创建人

    private String CreateTime;//创建时间

    private String Editor;//修改人

    private String Status;//状态0未处理 1 已处理

    private String IsDel;//是否删除 0未删除 1 删除

    private String ProjectId;

    private String ProjectName;

    private String BuildBookId;

    private String BuildBookName;

    private String type;

    private String typeName;

    private String FeedbackName; //0 旭客汇 1 旭客家 2 旭客通 3 旭客后台

    private String SystemSource; //'系统来源 1旭客家  2 旭客汇'

    private String keyWord; //'系统来源 1旭客家  2 旭客汇'

    private List<String> imgList;

    public String[]  toFeedbackTitle =  new String[]{
            "序号","反馈人","联系方式","项目","楼盘","问题类型",
            "反馈端","反馈时间","反馈问题描述",
            "状态","处理人","处理时间","处理描述"};


    public String[]  toAllFeedbackTitle =  new String[]{
            "序号","反馈人","联系方式","反馈人项目岗位","反馈项目","反馈模块","反馈时间",
            "问题描述","问题类型","状态",
            "处理人","处理时间","处理描述"};




    /**
     * 获取反馈的数据
     * @param
     * @return
     */
    public Object[] toFeedbackData(){
        return new Object[]{
                getNum(),getFeedBackUserName(),getFeedBackUserMobile(),getProjectName(),getBuildBookName(),getTypeName(),getFeedBackUserRole(),
                getCreateTime(),getProblemDesc(),getStatus(),getHandleUserName(),getHandleTime(),getHandleDesc()
        };
    }




    /**
     * 获取旭客汇反馈的数据
     * @param
     * @return
     */
    public Object[] toAllFeedbackData(){
        return new Object[]{
                getNum(),getFeedBackUserName(),getFeedBackUserMobile(),getFeedBackUserRole(),getProjectName(),getFeedbackName(),getCreateTime(),
                getProblemDesc(),getTypeName(),getStatus(),getHandleUserName(),getHandleTime(),getHandleDesc()
        };
    }
}
