package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @ClassName BuildingPoster
 * @Author wanggang
 * @Description //楼盘海报
 * @Date 2020/3/12 14:48
 **/
@Data
public class BuildingProblem {

    private String ID;

    //问题描述
    private String Problem;
    //问题答案
    private String Answer;
    //图片路径
    private String ProblemPhotoUrl;
    //项目ID
    private String ProjectId;
    //楼盘ID
    private String BuildBookID;
    //置顶时间
    private String TopTime;
    //是否启用
    private String Status;
    //创建人
    private String Creator;
    //创建时间
    private String CreateTime;
    //是否删除 0：否 1：是
    private String IsDel;
    //编辑人
    private String Editor;
    //编辑时间
    private String EditTime;

}
