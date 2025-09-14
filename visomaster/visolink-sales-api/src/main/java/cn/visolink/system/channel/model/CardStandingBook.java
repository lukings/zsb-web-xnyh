package cn.visolink.system.channel.model;

import lombok.Data;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description: 置业顾问名片台账导出
 * @Date: Created in 2020/5/15
 */
@Data
public class CardStandingBook {

    private int rownum;
    private String name;
    private String mobile;
    private String tag;
    private String signature;
    private String buildName;
    private String areaName;
    private String projectNum;
    private String viewNumber;
    private String viewNumbers;
    private String attentionNumber;
    private String customerNumber;
    private String imNumber;
    private String callNumber;
    private String shareNumber;
    private String hshareNumber;
    private String thshareNumber;
    private String likeNumber;
    private String replyDuration;
    private String replyRate;

    /**
     * 获取分析数据
     * @param
     * @return
     */
    public Object[] toExproData(){
        return new Object[]{
                getRownum(),getName(),getMobile(),getTag(),getSignature(),
                getBuildName(),getAreaName(),getProjectNum(),getViewNumber(),getViewNumbers(),getAttentionNumber(),
                getCustomerNumber(),getImNumber(),getCallNumber(),getShareNumber(),
                getHshareNumber(),getThshareNumber(),getLikeNumber(),getReplyDuration(),getReplyRate()
        };
    }
}
