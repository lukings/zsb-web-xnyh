package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @ClassName HouseAnalysis
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/11/25 10:49
 **/
@Data
public class HouseAnalysis {

    private String ID;

    private String BuildBookID;//楼盘ID

    private String ProjectID;//项目ID

    private String HouseTypeID;//户型ID

    private String ListIndex;//排序

    private String TagParentCode;//父级标签Code

    private String TagParentName;//父级标签

    private String TagDesc;//标签描述

    private String Creator;//创建人

}
