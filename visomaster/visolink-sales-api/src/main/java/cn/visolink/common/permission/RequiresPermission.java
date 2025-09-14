package cn.visolink.common.permission;

import java.lang.annotation.*;

/**
 * 权限检查注解
 * 用于标记需要权限检查的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * 权限标识，对应功能菜单的权限码
     */
    String value() default "";
    
    /**
     * 权限描述
     */
    String description() default "";
    
    /**
     * 是否必须检查权限，默认true
     */
    boolean required() default true;
}
