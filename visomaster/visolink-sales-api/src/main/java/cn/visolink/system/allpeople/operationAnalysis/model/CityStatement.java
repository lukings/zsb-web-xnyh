package cn.visolink.system.allpeople.operationAnalysis.model;

import lombok.Data;

/**
 * @ClassName CityStatement
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/7/7 14:03
 **/
@Data
public class CityStatement {

    private String rownum;//序号
    private String AreaName;//区域
    private String ProjectName;//项目
    private String ProjectId;//项目ID
    private String CityID;//城市ID
    private String CityName;//城市名称
    private String BuildCount;//楼盘数量
    private String PhotoCount;//首页轮播图数量（有效）
    private String RangePhotoCount;//区间内创建轮播图数量
    private String PhotoJumpCount;//轮播图跳转数量
    private String NewsCount;//新闻数量
    private String RangeNewsCount;//区间内创建新闻数量
    private String CreateNewsMeanTime;//创建新闻平均时长（天）
    private String ActivityCount;//活动数量
    private String RangeActivityCount;//区间内发布活动数量
    private String ReleaseActivityMeanTime;//发布活动平均时长（天）

    /**
     * 获取城市数据
     * @param
     * @return
     */
    public Object[] tocityData(){
        return new Object[]{
                getRownum(),getAreaName(),getCityName(),getBuildCount(),
                getPhotoCount(),getRangePhotoCount(),getPhotoJumpCount(),
                getNewsCount(),getRangeNewsCount(),
                getCreateNewsMeanTime(),getActivityCount(),getRangeActivityCount(),
                getReleaseActivityMeanTime()
        };
    }
    public String[]  cityTitle =  new String[]{
            "序号","区域/事业部","城市",
            "楼盘数量","首页轮播图总量","区间内创建轮播图数量",
            "首页轮播图跳转总量","新闻数量","区间内创建新闻数量",
            "创建新闻平均时长（天）","活动数量"
            ,"区间内发布活动数","发布活动平均时长（天）"};

}
