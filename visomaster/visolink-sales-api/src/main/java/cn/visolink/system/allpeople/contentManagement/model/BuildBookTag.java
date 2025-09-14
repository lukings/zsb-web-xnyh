package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @ClassName BuildBookTag
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/11/25 10:29
 **/
@Data
public class BuildBookTag {

    private String id;

    private String tagName;//标签名称

    private String tagParentCode;//父级标签Code

    private String tagParentName;//父级标签

    private String tagType;//标签类型（1：楼盘标签 2：户型标签）

    private String isShow;//是否展示

    private String buildBookId;//楼盘ID

    private String houseTypeId;//户型ID

    private String listIndex;//排序

    private String creator;//创建人
}
