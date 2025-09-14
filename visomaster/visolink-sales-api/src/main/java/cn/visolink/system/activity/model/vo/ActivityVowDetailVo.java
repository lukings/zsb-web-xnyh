package cn.visolink.system.activity.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/10/26 9:47
 */
@Data
public class ActivityVowDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String rowNum;

    private String activity_id;

    private String activity_name;

    private String activity_no;

    private String vow_award_name;

    private String vow_no;

    private String win_award_id;

    private String win_award_name;

    private String award_no;

    private String vow_time;

    private String vow_barrage;

    private String award_status;

    private String vow_open_time;

    private String operate_remark;

    private String operate_url;

    private String is_open_award;

    private String operator;

    private String operator_name;

    private String project_name;

    private String mobile;

    private String allmobile;

    private String name;

    private String barrage_status;

    private String operate_time;

    private String couponStatus;

    private String closure;

    private String closure_time;

    private String customerMobile;

    public Object[] toActivityHelpData(String isAll){
        String customerMobile = "";
        if ("1".equals(isAll)){
            customerMobile = getAllmobile();
        }else{
            customerMobile = getMobile();
        }
        return new Object[]{
                getRowNum(),getActivity_no(),getActivity_name(),getProject_name(),
                customerMobile,getName(),getVow_time(),getVow_no(),getVow_award_name(),getVow_barrage(),getVow_open_time(),getIs_open_award(),
                getAward_status(),getAward_no(),getWin_award_name(),getOperator_name(),getOperate_time(),getOperate_remark(),
                getCouponStatus(),getClosure(),getClosure_time()
        };
    }

    public String[]  activityHelpTitle =  new String[]{
            "序号","活动编号","活动名称","关联项目",
            "用户手机号","用户昵称","许愿时间","许愿奖品编号","许愿奖品名称",
            "许愿弹幕","开奖时间","开奖状态","中奖状态","中奖奖品编号","中奖奖品名称","中奖变更人","变更时间",
            "变更原因","奖品状态","核销人","核销时间"
    };












}
