package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @ClassName 户型素材
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/11/25 10:18
 **/
@Data
public class HouseImg {

    private String ID;

    private String BuildBookID;//楼盘ID

    private String ProjectID;//项目ID

    private String HouseTypeID;//户型ID

    private String ImgUrl;//素材链接

    private String TypeName;//分类（1：户型轮播图 2：户型视频）

    private String ListIndex;//排序

    private String Creator;//创建人
}
