package cn.visolink.system.projectmanager.model.requestmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/9 16:52
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseModel implements Serializable {

    @ApiModelProperty(name = "pageIndex", value = "页数")
    private Integer pageIndex;

    @ApiModelProperty(name = "pageSize", value = "页数大小")
    private Integer pageSize;

    @ApiModelProperty(name = "row", value = "行")
    private Integer row;
}

