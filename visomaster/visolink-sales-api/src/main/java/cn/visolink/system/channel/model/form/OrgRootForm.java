package cn.visolink.system.channel.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * ProjectCluesForm对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "orgroot对象")
public class OrgRootForm extends Page {

    private static final long serialVersionUID = 1L;

    private String projectId;

    private String orgCategory;

    private String orgId;

    private String employeeName;

}
