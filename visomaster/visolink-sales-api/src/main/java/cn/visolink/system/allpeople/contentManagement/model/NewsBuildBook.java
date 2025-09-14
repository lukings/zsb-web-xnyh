package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @Author: zht
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2021/6/10
 */
@Data
public class NewsBuildBook {

    private String id;              //主键id
    private String newsId;//新闻Id
    private String buildBookId;//楼盘id
    private String buildBookName;//楼盘名称
    private String status;//是否启用：1 启用 0 禁用
    private String isDel;//是否删除：1 删除 0 未删除
    private String listIndex;//排序字段
    private String createTime;//创建时间
    private String creator;//创建人
    private String editTime;//修改时间
    private String editor;//修改人
}
