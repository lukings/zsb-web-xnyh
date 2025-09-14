package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @ClassName BuildingPoster
 * @Author wanggang
 * @Description //楼盘海报
 * @Date 2020/3/12 14:48
 **/
@Data
public class BuildingPoster {

    private String ID;

    private String PosterTitle;

    private String PosterWCodeUrl;

    private String PosterPhotoUrl;

    private String RegisterCnt;

    private String ProjectId;

    private String BuildBookID;

    private String Status;

    private String Creator;

    private String CreateTime;

    private String IsDel;
}
