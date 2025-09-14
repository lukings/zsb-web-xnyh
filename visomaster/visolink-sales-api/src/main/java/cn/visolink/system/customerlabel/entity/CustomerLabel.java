package cn.visolink.system.customerlabel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 个人标签信息表
 *
 * @author system
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_customer_label")
public class CustomerLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "Id", type = IdType.INPUT)
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

    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDel;
}
