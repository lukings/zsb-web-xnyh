package cn.visolink.system.customerlabel.bo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 批量添加客户标签BO
 *
 * @author system
 * @since 2024-01-01
 */
@Data
public class BatchCustomerLabelBO {

    /**
     * 标签名称列表
     */
    @NotEmpty(message = "标签列表不能为空")
    @Size(max = 100, message = "一次最多只能添加100个标签")
    @Valid
    private List<String> customerLabels;
}
