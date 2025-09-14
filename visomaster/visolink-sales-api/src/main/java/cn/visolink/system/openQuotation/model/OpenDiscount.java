package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OpenDiscount
 * @Author wanggang
 * @Description //折扣
 * @Date 2021/1/4 10:08
 **/
@Data
public class OpenDiscount implements Serializable {

    private String id;

    private String projectId;

    private String projectName;

    private String activityId;

    private String discountId;

    private String discountName;

    private String creator;

    private String createTime;

}
