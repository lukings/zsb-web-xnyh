package cn.visolink.system.projectmanager.model.requestmodel;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/23 11:52
 * @description
 * @Version 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class PriceDto extends BaseModel implements Serializable {

    /**
     * 申请名称
     */
    private String applyName;
    
    /**
     *   0:标准价录入 1:低价录入
     */
    private Integer type;
}

