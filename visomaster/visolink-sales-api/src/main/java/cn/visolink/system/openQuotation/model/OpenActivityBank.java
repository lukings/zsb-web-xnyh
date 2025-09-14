package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName OpenActivityBank
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/1/14 11:44
 **/
@Data
public class OpenActivityBank implements Serializable {

    private String openActivityId;//开盘活动ID

    private String projectFid;//分期项目ID

    private String projectFname;//分期项目名

    private String bankId;//商户ID

    private String creator;

    private List<ProBank> banks;//分期下商户集合
}
