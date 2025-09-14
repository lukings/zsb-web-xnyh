package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @ClassName BuildBookPeriphery
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/11/17 19:47
 **/
@Data
public class BuildBookPeriphery {

    private String id;

    private String projectId;//项目ID

    private String buildBookId;//楼盘ID

    private String title;//周边名称

    private String address;//地址

    private String tel;//电话

    private String lat;//纬度

    private String lng;//经度

    private String distance;//距离

    private String adType;//类型（1:学校 2:交通 3:购物 4:餐饮 5:医院）

    private String creator;//创建人

    private String createTime;//创建时间
}
