package cn.visolink.system.projectmanager.model;

import cn.visolink.system.projectmanager.model.requestmodel.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Mr.Yu
 * @Date: 2021/12/7 14:06
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderTemplate extends BaseModel {

    private static final long serialVersionUID = -78426168932284087L;

    private Integer id;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 模板路径
     */
    private String subTemplateUrl;

    /**
     *   模板路径集合 使用逗号分割
     */
    private List<String> subTemplateUrlList;

    /**
     * 模板名称
     */
    private String subTemplateName;

    /**
     * 创建人ID
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String updator;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 0禁用 1启用
     */
    private Integer status;

    /**
     * 是否删除
     */
    private Integer isDel;

    /**
     *   x_open_activity 表的活动名称
     */
    private String sxActivityName;

    /**
     * 项目名称
     */
    private String projectName;
}

