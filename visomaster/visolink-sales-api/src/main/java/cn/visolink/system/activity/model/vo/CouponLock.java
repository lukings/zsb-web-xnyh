package cn.visolink.system.activity.model.vo;

import lombok.Data;

/**
 * @author liming
 * <p>
 * created at 2021/3/3 18:37
 */
@Data
public class CouponLock {

    private Integer isLock;

    private String couponId;

    private Integer isSystem;
}
