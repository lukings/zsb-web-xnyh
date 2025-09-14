package cn.visolink.system.customerlabel.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 客户标签BO
 *
 * @author system
 * @since 2024-01-01
 */
@Data
public class CustomerLabelBO {

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String customerLabel;
}
