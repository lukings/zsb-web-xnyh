package cn.visolink.system.customerlabel.vo;

import lombok.Data;

import java.util.Date;

/**
 * 客户标签VO
 *
 * @author system
 * @since 2024-01-01
 */
@Data
public class CustomerLabelVO {

    /**
     * ID
     */
    private String id;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 标签名称
     */
    private String customerLabel;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String editor;

    /**
     * 更新时间
     */
    private Date editTime;
}
