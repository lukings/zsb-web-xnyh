package cn.visolink.system.openQuotation.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Mr.Yu
 * @Date: 2021/12/16 16:36
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@ToString
@Data
public class OfflineCertificate implements Serializable {

    /**
     * 主键id
     */
    private String id;

    /**
     * 区域
     */
    private String region;

    /**
     * 项目id
     */
    private String project_id;

    /**
     * 项目名称
     */
    private String project_name;

    /**
     * 活动id
     */
    private String activity_id;

    /**
     * 认购活动名称
     */
    private String activity_name;

    /**
     *   房间id
     */
    private String room_id;

    /**
     * 房间名称
     */
    private String room_name;

    /**
     * 认购人名称
     */
    private String client_name;

    /**
     * 认购人手机号
     */
    private String client_mobile;

    /**
     * 提交人
     */
    private String submit;

    /**
     * 身份证正面
     */
    private String card_on_pic;

    /**
     * 身份证反面
     */
    private String card_dn_pic;

    /**
     * 支付凭证图片
     */
    private String certificate_pic;

    /**
     * 审批状态（1：待审核 2：审批通过, 3: 驳回）
     */
    private Integer status;

    /**
     *   审批状态名称
     */
    private String status_name;

    /**
     * 提交审核时间
     */
    private String create_time;

    /**
     * 审批时间
     */
    private String approve_time;

    /**
     * 审批人ID
     */
    private String approve_id;

    /**
     * 审批人名称
     */
    private String approve_name;

    /**
     * 交易id
     */
    private String trade_guid;

    /**
     * 驳回原因
     */
    private String rejection_reason;

    /**
     * 是否删除（1：是 0：否）
     */
    private Integer is_del;

    /**
     * 行序号
     */
    private String rownum;

    /**
     * 获取数据
     *
     * @return
     */
    public Object[] toCertificateData() {
        return new Object[]{
                getRownum(), getRegion(), getProject_name(), getActivity_id(), getActivity_name(),
                getRoom_name(), getClient_name(),
                getClient_mobile(), getSubmit(), getCard_on_pic(), getCard_dn_pic(),
                getCertificate_pic(), getStatus_name(), getCreate_time(),
                getApprove_name(), getApprove_time()
        };
    }

    /**
     * excel表头
     */
    public String[] certificateTitle = new String[]{
            "序号", "区域", "项目", "认购活动编号", "认购活动名称",
            "认购房间", "认购人姓名", "认购人手机号", "提交人",
            "身份证正面照", "身份证反面照", "支付凭证图片", "审核状态", "提交审核时间", "审核人", "审核时间"};

}

