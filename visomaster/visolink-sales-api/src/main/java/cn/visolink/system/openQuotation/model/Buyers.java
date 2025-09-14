package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName Buyers
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/1/8 15:51
 **/
@Data
public class Buyers implements Serializable {

    private String cstName;//客户姓名

    private String cstPhone;//客户手机号

    private String cstCardTypeName;//客户证件类型

    private String cstCardId;//客户证件号

    private String address;//客户地址

    private String salesName;//置业顾问名称

}
