package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ProBank
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/1/14 13:55
 **/
@Data
public class ProBank implements Serializable {

    private String bankId;

    private String projectFid;//分期ID

    private String collBank;//收款银行
}
