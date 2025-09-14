package cn.visolink.system.allpeople.operationAnalysis.model;

import lombok.Data;

/**
 * @ClassName ProjectStatement
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/7/9 9:30
 **/
@Data
public class ProjectStatement {

    private String rownum;//序号
    private String AreaName;//区域
    private String ProjectName;//项目
    private String ProjectNum;//项目编码
    private String ProjectId;//项目ID
    private String PhotoCount;//楼书轮播图数量（有效）
    private String VideoCount;//视频数量
    private String VrCount;//VR数量
    private String HouseImgCount;//户型图数量
    private String PosterCount;//海报图片数量
    private String RangePosterCount;//区间内上传海报图片数量
    private String CreatePosterCount;//生成海报数量
    private String CardCount;//智慧名片维护数量
    private String CardCollectCstCount;//智慧名片关注人数
    private String CardCollectCount;//智慧名片关注次数
    private String CardCancelCollectCstCount;//智慧名片取消关注人数
    private String CardCancelCollectCount;//智慧名片取消关注次数
    private String ConsultingCstCount;//咨询人数
    private String ConsultingCount;//咨询次数
    private String CallCstCount;//拨打人数
    private String CallCount;//拨打次数
    private String ConsultingVisitCount;//咨询转来访总量
    private String VisitRate;//转访率
    private String ConsultingTurnoverCount;//咨询转成交总量
    private String TurnoverRate;//成交率

    private String dynamicAllCnt;//楼盘动态累计量
    private String dynamicReleaseCnt;//楼盘动态发布量
    private String dynamicReleaseTime;//楼盘动态发布时长
    /**
     * 获取项目数据
     * @param
     * @return
     */
    public Object[] toProData(){
        return new Object[]{
                getRownum(),getAreaName(),getProjectName(),getProjectNum(),
                getPhotoCount(),getVideoCount(),getVrCount(),
                getHouseImgCount(),getPosterCount(),getRangePosterCount(),
                getCreatePosterCount(),getCardCount(),getCardCollectCstCount(),
                getCardCollectCount(),getCardCancelCollectCstCount(),
                getCardCancelCollectCount(),getConsultingCstCount(),getConsultingCount(),
                getCallCstCount(),getCallCount(),getConsultingVisitCount(),getVisitRate(),
                getConsultingTurnoverCount(),getTurnoverRate(),getDynamicAllCnt(),getDynamicReleaseCnt(),
                getDynamicReleaseTime()
        };
    }
    public String[]  proTitle =  new String[]{
            "序号","区域/事业部","项目","项目编码",
            "楼书轮播图数量","视频数量","VR数量",
            "户型图数量","海报图片数量","区间内上传海报图片数量",
            "生成海报人数","智慧名片维护数量"
            ,"智慧名片收藏人数","智慧名片收藏次数"
            ,"智慧名片取消收藏人数","智慧名片取消收藏次数"
            ,"咨询人数","咨询次数"
            ,"拨打人数","拨打次数"
            ,"咨询转来访总量","转访率"
            ,"咨询转成交总量","成交率"
            ,"动态数量","区间内发布动态数量","发布动态平均时长（天）"};
}
